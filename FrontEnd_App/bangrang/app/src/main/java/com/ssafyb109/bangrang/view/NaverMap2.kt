package com.ssafyb109.bangrang.view

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.CircleOverlay
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PolygonOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.naver.maps.map.overlay.GroundOverlay
import com.naver.maps.map.overlay.OverlayImage
import com.ssafyb109.bangrang.R
import com.ssafyb109.bangrang.view.utill.location.BusanLocation
import com.ssafyb109.bangrang.view.utill.location.ChungbukLocation
import com.ssafyb109.bangrang.view.utill.location.ChungnamLocation
import com.ssafyb109.bangrang.view.utill.location.DaeguLocation
import com.ssafyb109.bangrang.view.utill.location.DaejeonLocation
import com.ssafyb109.bangrang.view.utill.location.GangwonLocation
import com.ssafyb109.bangrang.view.utill.location.GwangjuLocation
import com.ssafyb109.bangrang.view.utill.location.GyeongbukLocation
import com.ssafyb109.bangrang.view.utill.location.GyeonggiLocation
import com.ssafyb109.bangrang.view.utill.location.GyeongnamLocation
import com.ssafyb109.bangrang.view.utill.location.IncheonLocation
import com.ssafyb109.bangrang.view.utill.location.JejuLocation
import com.ssafyb109.bangrang.view.utill.location.JeollabukLocation
import com.ssafyb109.bangrang.view.utill.location.JeollanamLocation
import com.ssafyb109.bangrang.view.utill.location.SejongLocation
import com.ssafyb109.bangrang.view.utill.location.SeoulLocation
import com.ssafyb109.bangrang.view.utill.location.UlsanLocation

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun NaverMap2(height: Dp = Dp.Unspecified, isCovered: Boolean) {
    val center = LatLng(36.3555, 127.2986)

    val koreaCoords = listOf(
        LatLng(38.3624684, 128.2379138),
        LatLng(38.3018758, 127.9705808),
        LatLng(38.2935287, 127.5842282),
        LatLng(38.2850984, 127.1493526),
        LatLng(38.0524613, 126.9359518),
        LatLng(37.8961798, 126.7812670),
        LatLng(37.7505272, 126.6546398),
        LatLng(37.7965884, 126.2526963),
        LatLng(37.7092491, 126.1959194),
        LatLng(37.6529023, 125.6951187),
        LatLng(34.6702210, 125.4130944),
        LatLng(34.1848527, 126.0472751),
        LatLng(34.0839857, 126.9246640),
        LatLng(34.4098303, 127.7918244),
        LatLng(34.7505469, 128.7252852),
        LatLng(35.1066589, 129.2030048),
        LatLng(35.4791976, 129.4563472),
        LatLng(36.1660760, 129.6298764),
        LatLng(36.1348832, 129.4747775),
        LatLng(36.5220197, 129.4713857),
        LatLng(36.7441575, 129.4987796),
        LatLng(37.0648228, 129.4421891),
        LatLng(37.2787281, 129.3562162),
        LatLng(37.6723970, 129.0770241),
        LatLng(38.0467423, 128.7396749),
        LatLng(38.6025249, 128.4137193)
    )

    val distance = 0.0009 // 대략 100m
    val squareHole = listOf(
        LatLng(center.latitude - distance, center.longitude - distance), // 남서
        LatLng(center.latitude - distance, center.longitude + distance), // 남동
        LatLng(center.latitude + distance, center.longitude + distance), // 북동
        LatLng(center.latitude + distance, center.longitude - distance)  // 북서
    )



    val mapProperties by remember {
        mutableStateOf(
            MapProperties(maxZoom = 50.0, minZoom = 5.0, locationTrackingMode = LocationTrackingMode.Follow)
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(isLocationButtonEnabled = true)
        )
    }


    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition(center, 17.0)
    }

    val groundOverlay = GroundOverlay()
    groundOverlay.bounds = LatLngBounds(
        LatLng(37.566351, 126.977234), LatLng(37.568528, 126.979980))
    groundOverlay.image = OverlayImage.fromResource(R.drawable.black256)
    groundOverlay.map = null


    Box(
        Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        NaverMap(
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            locationSource = rememberFusedLocationSource()
        ) {
            if(isCovered) {
                PolygonOverlay(
                    coords = koreaCoords, // 대한민국 경계를 기준으로 하는 코너 좌표
                    holes = listOf(squareHole), // 중심점을 기준으로 한 정사각형 구멍
                    color = Color.DarkGray, // 다각형의 색
                    outlineWidth = 2.dp, // 외곽선의 두께
                    outlineColor = Color.Black, // 외곽선의 색
                    tag = null,
                    visible = true,
                    minZoom = 0.0,
                    minZoomInclusive = true,
                    maxZoom = 22.0,
                    maxZoomInclusive = true,
                    zIndex = 0,
                    globalZIndex = 0,
                )
            }
        }
    }
}