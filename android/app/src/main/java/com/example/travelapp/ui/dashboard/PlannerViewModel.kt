package com.example.travelapp.ui.planner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelapp.data.model.*
import com.example.travelapp.data.repository.PlanRepository
import com.example.travelapp.data.repository.PropertyRepository
import com.example.travelapp.data.repository.UserRepository
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Named

sealed class PlannerUiState {
    object Idle : PlannerUiState()
    data class Interpreting(val message: String = "Đang phân tích điểm đến...") : PlannerUiState()
    data class Generating(val message: String = "Đang tạo lịch trình...") : PlannerUiState()
    data class Success(
        val plan: Plan,
        val matchedProperties: List<Property>
    ) : PlannerUiState()
    data class Error(val message: String) : PlannerUiState()
}

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val planRepository: PlanRepository,
    private val userRepository: UserRepository,
    @Named("interpreter") private val interpreterModel: GenerativeModel,
    @Named("planner") private val plannerModel: GenerativeModel
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlannerUiState>(PlannerUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _savingState = MutableStateFlow(false)
    val savingState = _savingState.asStateFlow()

    fun generatePlan(
        destination: String,
        duration: Int,
        budget: String,
        interests: String
    ) {
        viewModelScope.launch {
            try {
                // ===== STAGE 1: Interpret destination via Gemini =====
                _uiState.value = PlannerUiState.Interpreting()
                Log.d("PlannerVM", "Stage 1: Interpreting destination '$destination'")

                val interpretPrompt = """
                    Người dùng nhập điểm đến du lịch: "$destination"
                    
                    Hãy phân tích điểm đến này và trả về JSON chứa hai trường:
                    1. "desName": Tên tỉnh/thành phố chuẩn trùng khớp với một trong các giá trị sau trong cơ sở dữ liệu:
                       - "Thành phố Hà Nội", "Thành phố Hồ Chí Minh", "Thành phố Hải Phòng", "Thành phố Đà Nẵng", "Thành phố Cần Thơ", "Thành phố Huế"
                       - "Tỉnh An Giang", "Tỉnh Bắc Ninh", "Tỉnh Cao Bằng", "Tỉnh Cà Mau", "Tỉnh Điện Biên", "Tỉnh Đắk Lắk", "Tỉnh Đồng Nai", "Tỉnh Đồng Tháp"
                       - "Tỉnh Gia Lai", "Tỉnh Hà Tĩnh", "Tỉnh Hưng Yên", "Tỉnh Khánh Hòa", "Tỉnh Lai Châu", "Tỉnh Lào Cai", "Tỉnh Lâm Đồng", "Tỉnh Lạng Sơn"
                       - "Tỉnh Nghệ An", "Tỉnh Ninh Bình", "Tỉnh Phú Thọ", "Tỉnh Quảng Ngãi", "Tỉnh Quảng Ninh", "Tỉnh Quảng Trị", "Tỉnh Sơn La"
                       - "Tỉnh Thanh Hóa", "Tỉnh Thái Nguyên", "Tỉnh Tuyên Quang", "Tỉnh Tây Ninh", "Tỉnh Vĩnh Long"
                       Nếu không khớp với tỉnh nào ở trên, hãy tự chọn tỉnh/thành phố phù hợp nhất.
                    
                    2. "cityName": Tên thành phố hoặc khu vực cụ thể bằng tiếng Việt có dấu để lên lịch trình chi tiết (ví dụ: "Đà Lạt", "Nha Trang", "Mũi Né", "Sapa", "Hồ Chí Minh", "Hà Nội").

                    Chỉ trả về định dạng JSON:
                    {"desName": "Tên_Tỉnh_Thành_Từ_Danh_Sách", "cityName": "Tên_Thành_Phố_Khu_Vực"}
                    
                    Ví dụ:
                    - "da lat" -> {"desName": "Tỉnh Lâm Đồng", "cityName": "Đà Lạt"}
                    - "nha trang" -> {"desName": "Tỉnh Khánh Hòa", "cityName": "Nha Trang"}
                    - "saigon" hoặc "hcm" -> {"desName": "Thành phố Hồ Chí Minh", "cityName": "Hồ Chí Minh"}
                    - "ha noi" -> {"desName": "Thành phố Hà Nội", "cityName": "Hà Nội"}
                """.trimIndent()

                val interpretResponse = interpreterModel.generateContent(interpretPrompt)
                val interpretedText = interpretResponse.text ?: ""
                Log.d("PlannerVM", "Stage 1 result: $interpretedText")

                // Parse the desName and cityName from response
                val (interpretedDesName, interpretedCityName) = try {
                    val jsonStr = interpretedText.trim()
                        .removePrefix("```json").removePrefix("```")
                        .removeSuffix("```").trim()
                    val obj = JSONObject(jsonStr)
                    Pair(obj.getString("desName"), obj.getString("cityName"))
                } catch (e: Exception) {
                    Log.w("PlannerVM", "Failed to parse interpreted destination, using raw input", e)
                    Pair(destination, destination) // Fallback to raw user input
                }
                Log.d("PlannerVM", "Interpreted destination: desName='$interpretedDesName', cityName='$interpretedCityName'")

                // ===== STAGE 2: Query Firestore for matching properties =====
                val matchedProperties = propertyRepository.getPropertiesByDestination(interpretedDesName)
                Log.d("PlannerVM", "Stage 2: Found ${matchedProperties.size} properties in '$interpretedDesName'")

                // ===== STAGE 3: Generate structured plan via Gemini =====
                _uiState.value = PlannerUiState.Generating()

                val hotelContext = if (matchedProperties.isNotEmpty()) {
                    "KHÁCH SẠN CÓ SẴN TRONG HỆ THỐNG:\n" +
                    matchedProperties.joinToString("\n") { prop ->
                        "- ${prop.name} | ID: ${prop.proId} | Giá: ${prop.price}đ/đêm | Đánh giá: ${prop.averageRating}⭐ | Địa chỉ: ${prop.address}"
                    }
                } else {
                    "Không có khách sạn nào trong hệ thống tại điểm đến này."
                }

                val finalBudget = budget.ifBlank { "Không yêu cầu cụ thể" }
                val finalInterests = interests.ifBlank { "Không yêu cầu cụ thể" }

                val planPrompt = """
                    Bạn là trợ lý du lịch AI chuyên nghiệp.
                    
                    $hotelContext
                    
                    Hãy lên lịch trình du lịch $interpretedCityName trong $duration ngày.
                    Ngân sách: $finalBudget
                    Sở thích: $finalInterests
                    
                    Nhiệm vụ: Bạn phải đề xuất khách sạn lưu trú cho chuyến đi.
                    - ƯU TIÊN gợi ý khách sạn từ danh sách "KHÁCH SẠN CÓ SẴN TRONG HỆ THỐNG" ở trên. Trích xuất chính xác propertyId từ hệ thống và điền vào trường suggestedHotel.
                    - Bạn có thể gợi ý cùng một khách sạn cho nhiều ngày nếu thích hợp.
                    
                    Trả về JSON theo schema sau:
                    {
                      "days": [
                        {
                          "dayNumber": 1,
                          "title": "Ngày 1: ...",
                          "activities": [
                            { "time": "08:00", "title": "...", "description": "...", "estimatedCost": "...", "location": "..." }
                          ],
                          "suggestedHotel": { "propertyId": "ID_TỪ_DANH_SÁCH", "name": "...", "pricePerNight": 500000, "rating": 4.5 }
                        }
                      ]
                    }
                    Nếu không có khách sạn nào trong danh sách trên, để suggestedHotel là null.
                    Chỉ trả về JSON, không giải thích thêm.
                """.trimIndent()

                Log.d("PlannerVM", "Stage 3: Generating plan...")
                val planResponse = plannerModel.generateContent(planPrompt)
                val planText = planResponse.text ?: ""
                Log.d("PlannerVM", "Stage 3 result: $planText")

                // Parse JSON response into Plan data class
                val plan = parsePlanFromJson(
                    jsonText = planText,
                    destination = interpretedCityName,
                    duration = duration,
                    budget = finalBudget,
                    interests = finalInterests
                )

                _uiState.value = PlannerUiState.Success(
                    plan = plan,
                    matchedProperties = matchedProperties
                )

            } catch (e: Exception) {
                Log.e("PlannerVM", "Error generating plan", e)
                _uiState.value = PlannerUiState.Error("Lỗi: ${e.localizedMessage}")
            }
        }
    }

    fun savePlan(plan: Plan) {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _savingState.value = true
            try {
                val planWithUser = plan.copy(
                    userId = uid,
                    createdAt = System.currentTimeMillis()
                )
                // Collect all unique property IDs from suggested hotels
                val propertyIds = plan.days
                    .mapNotNull { it.suggestedHotel?.propertyId }
                    .filter { it.isNotEmpty() }
                    .distinct()

                val finalPlan = planWithUser.copy(suggestedPropertyIds = propertyIds)
                val savedId = planRepository.savePlan(finalPlan)
                Log.d("PlannerVM", "Plan saved with ID: $savedId")
            } catch (e: Exception) {
                Log.e("PlannerVM", "Error saving plan", e)
            } finally {
                _savingState.value = false
            }
        }
    }

    fun resetState() {
        _uiState.value = PlannerUiState.Idle
    }

    private fun parsePlanFromJson(
        jsonText: String,
        destination: String,
        duration: Int,
        budget: String,
        interests: String
    ): Plan {
        val cleanJson = jsonText.trim()
            .removePrefix("```json").removePrefix("```")
            .removeSuffix("```").trim()

        val jsonObject = JSONObject(cleanJson)
        val daysArray = jsonObject.getJSONArray("days")
        val days = mutableListOf<PlanDay>()

        for (i in 0 until daysArray.length()) {
            val dayObj = daysArray.getJSONObject(i)
            val activitiesArray = dayObj.getJSONArray("activities")
            val activities = mutableListOf<PlanActivity>()

            for (j in 0 until activitiesArray.length()) {
                val actObj = activitiesArray.getJSONObject(j)
                activities.add(
                    PlanActivity(
                        time = actObj.optString("time", ""),
                        title = actObj.optString("title", ""),
                        description = actObj.optString("description", ""),
                        estimatedCost = actObj.optString("estimatedCost", ""),
                        location = actObj.optString("location", "")
                    )
                )
            }

            val suggestedHotel = if (!dayObj.isNull("suggestedHotel")) {
                val hotelObj = dayObj.getJSONObject("suggestedHotel")
                SuggestedHotel(
                    propertyId = hotelObj.optString("propertyId", ""),
                    name = hotelObj.optString("name", ""),
                    pricePerNight = hotelObj.optDouble("pricePerNight", 0.0),
                    rating = hotelObj.optDouble("rating", 0.0).toFloat()
                )
            } else null

            days.add(
                PlanDay(
                    dayNumber = dayObj.optInt("dayNumber", i + 1),
                    title = dayObj.optString("title", "Ngày ${i + 1}"),
                    activities = activities,
                    suggestedHotel = suggestedHotel
                )
            )
        }

        return Plan(
            destination = destination,
            duration = duration,
            budget = budget,
            interests = interests,
            days = days
        )
    }
}
