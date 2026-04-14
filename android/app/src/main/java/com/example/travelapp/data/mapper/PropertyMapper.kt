package com.example.travelapp.data.mapper

import com.example.travelapp.data.remote.dto.*
import com.example.travelapp.domain.model.*

fun PropertyDTO.toDomain(): Property {
    return Property(
        proId = proId,
        ownerId = ownerId,
        name = name,
        type = type,
        desId = desId,
        desName = desName,
        address = address,
        lat = lat,
        lng = lng,
        description = description,
        averageRating = averageRating,
        reviewCount = reviewCount,
        status = status,
        tags = tags,
        images = images.map { it.toDomain() },
        hotelInfo = hotelInfo?.toDomain(),
        activityInfo = activityInfo?.toDomain()
    )
}

fun PropertyImageDTO.toDomain(): PropertyImage {
    return PropertyImage(
        url = url,
        isPrimary = isPrimary
    )
}

fun HotelInfoDTO.toDomain(): HotelInfo {
    return HotelInfo(
        checkInTime = checkInTime,
        checkOutTime = checkOutTime,
        policy = policy
    )
}

fun ActivityInfoDTO.toDomain(): ActivityInfo {
    return ActivityInfo(
        duration = duration,
        maxPax = maxPax
    )
}

fun Property.toDTO(): PropertyDTO {
    return PropertyDTO(
        proId = proId,
        ownerId = ownerId,
        name = name,
        type = type,
        desId = desId,
        desName = desName,
        address = address,
        lat = lat,
        lng = lng,
        description = description,
        averageRating = averageRating,
        reviewCount = reviewCount,
        status = status,
        tags = tags,
        images = images.map { it.toDTO() },
        hotelInfo = hotelInfo?.toDTO(),
        activityInfo = activityInfo?.toDTO()
    )
}

fun PropertyImage.toDTO(): PropertyImageDTO {
    return PropertyImageDTO(
        url = url,
        isPrimary = isPrimary
    )
}

fun HotelInfo.toDTO(): HotelInfoDTO {
    return HotelInfoDTO(
        checkInTime = checkInTime,
        checkOutTime = checkOutTime,
        policy = policy
    )
}

fun ActivityInfo.toDTO(): ActivityInfoDTO {
    return ActivityInfoDTO(
        duration = duration,
        maxPax = maxPax
    )
}