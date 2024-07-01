package com.linx.playAndroid.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.linx.playAndroid.KeyNavigationRoute
import com.linx.playAndroid.model.ArticleListData
import com.linx.playAndroid.public.Banner
import com.linx.playAndroid.public.HomeCardItemContent
import com.linx.playAndroid.public.SwipeRefreshContent
import com.linx.playAndroid.public.getAuthor
import com.linx.playAndroid.viewModel.HomeViewModel

/**
 * 首页页面
 */
@Composable
fun HomeCompose(navHostController: NavHostController) {

    val homeViewModel: HomeViewModel = viewModel()

    val homeListData = homeViewModel.homeListData.collectAsLazyPagingItems()

    homeViewModel.getBannerData()

    val bannerListData = homeViewModel.bannerListData.observeAsState()

    //获取置顶数据列表
    homeViewModel.getArticleTopListData()

    val articleTopData = homeViewModel.articleTopList.observeAsState()
    /*SwipeRefreshList(collectAsLazyPagingItems = homeListData) {
        item {
            //轮播图
            Banner(bannerListData.value) { link ->
                navHostController.navigate("${KeyNavigationRoute.WEBVIEW.route}?url=$link")
            }
        }

        //置顶数据
        articleTopData.value?.let { listData ->
            items(listData) { data ->
                SimpleCard(cardHeight = 120.dp) {
                    data.apply {
                        HomeCardItemContent(
                            getAuthor(author, shareUser),
                            fresh,
                            true,
                            niceDate ?: "刚刚",
                            title ?: "",
                            superChapterName ?: "未知",
                            collect
                        ) {
                            navHostController.navigate("${KeyNavigationRoute.WEBVIEW.route}?url=$link")
                        }
                    }
                }
            }
        }

    }*/
    //首页页面的内容
    val articleTopList = articleTopData.value ?: emptyList()
    val state = homeViewModel.homeLazyListState
    SwipeRefreshContent(
        lazyPagingListData = homeListData,
        state = state,
        content = {
            item {
                //轮播图
                Banner(bannerListData.value) { link ->
                    navHostController.navigate("${KeyNavigationRoute.WEBVIEW.route}?url=$link")
                }
            }
            items(count = articleTopList.size) { index ->
                val item = articleTopList[index]
                HomeCardItem(item, navHostController)
            }
        }
    ) { index ,item->
        HomeCardItem(item, navHostController)
    }

}

@Composable
private fun HomeCardItem(data: ArticleListData, navHostController: NavHostController) {
    data.apply {
        HomeCardItemContent(
            getAuthor(author, shareUser),
            fresh,
            true,
            niceDate ?: "刚刚",
            title ?: "",
            superChapterName ?: "未知",
            collect
        ) {
            navHostController.navigate("${KeyNavigationRoute.WEBVIEW.route}?url=$link")
        }
    }
}