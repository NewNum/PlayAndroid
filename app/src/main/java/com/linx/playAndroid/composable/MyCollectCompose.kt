package com.linx.playAndroid.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.linx.playAndroid.model.MyCollectData
import com.linx.playAndroid.public.AppBar
import com.linx.playAndroid.public.BaseScreen
import com.linx.playAndroid.public.HomeCardItemContent
import com.linx.playAndroid.public.SwipeRefreshContent
import com.linx.playAndroid.public.getAuthor
import com.linx.playAndroid.viewModel.MyCollectViewModel

/**
 * 我的收藏页面
 */
@Composable
fun MyCollectCompose(navHostController: NavHostController) {

    val myCollectViewModel: MyCollectViewModel = viewModel()

    //布局
    MyCollectScreen(navHostController, myCollectViewModel)

}

/**
 * 我的收藏页面布局
 */
@Composable
private fun MyCollectScreen(
    navHostController: NavHostController,
    myCollectViewModel: MyCollectViewModel
) {

    //获取我的收藏列表数据
    val myCollectListData = myCollectViewModel.myCollectListData.collectAsLazyPagingItems()

    BaseScreen {
        Scaffold(
            topBar = {
                AppBar("我的收藏", leftIcon = Icons.Default.ArrowBack, onLeftClick = {
                    navHostController.navigateUp()
                })
            },
            content = { paddingValues: PaddingValues ->
                SwipeRefreshContent(
                    modifier = Modifier.padding(paddingValues),
                    lazyPagingListData = myCollectListData
                ) { index, item ->
                    CollectCardItem(data = item)
                }
            }
        )
    }

}


@Composable
private fun CollectCardItem(data: MyCollectData) {
    data.apply {
        HomeCardItemContent(
            getAuthor(author, null),
            false,
            false,
            niceDate ?: "刚刚",
            title ?: "",
            chapterName ?: "未知",
            true
        )
    }
}