package com.linx.playAndroid.public

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.linx.common.widget.sleepTime
import com.linx.playAndroid.public.paging.PagingStateUtil

/**
 * 带刷新头的Card布局
 * LazyPagingItems<T>
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : Any> SwipeRefreshContent(
    lazyPagingListData: LazyPagingItems<T>,
    cardHeight: Dp = 120.dp,
    state: LazyListState = rememberLazyListState(),
    header: @Composable (() -> Unit)? = null,
    itemContent: @Composable (index: Int, data: T) -> Unit
) {
    var refreshing by remember { mutableStateOf(false) }
    val updateRefreshStatus = { refresh: Boolean ->
        refreshing = refresh
    }

    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        //刷新数据
        lazyPagingListData.refresh()
    })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
    ) {
        Column(Modifier.fillMaxSize()) {
            header?.invoke()
            PagingStateUtil().PagingUtil(
                lazyPagingListData,
                refreshing,
                updateRefreshStatus,
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize(), state = state) {
                    items(count = lazyPagingListData.itemCount) { index ->
                        val item = lazyPagingListData[index]
                        SimpleCard(cardHeight = cardHeight) {
                            itemContent(index, item!!)
                        }
                    }
                }
            }
        }
//列表数据
        PullRefreshIndicator(
            refreshing = refreshing,
            state = refreshState,
            Modifier.fillMaxSize()
        )
    }
}

/**
 * 带刷新头的Card布局
 * LazyPagingItems<T>
 * Card高度自适应
 */
@Composable
fun <T : Any> SwipeRefreshContent(
    viewModel: ViewModel,
    listData: List<T>?,
    state: LazyListState = rememberLazyListState(),
    noData: () -> Unit,
    content: @Composable (data: T) -> Unit
) {

    if (listData == null) return

    if (listData.isEmpty()) {
        ErrorComposable("暂无数据，请点击重试") {
            noData()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {

        val refreshState = rememberSwipeRefreshState(false)

        SwipeRefresh(
            state = refreshState,
            onRefresh = {
                //显示刷新头
                refreshState.isRefreshing = true
                //刷新数据
                noData()
                viewModel.sleepTime(3000) {
                    refreshState.isRefreshing = false
                }
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = state) {
                itemsIndexed(listData) { index, data ->
                    SimpleCard {
                        content(data)
                    }
                }
            }
        }

    }
}