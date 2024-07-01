package com.linx.playAndroid.public

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.paging.CombinedLoadStates
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.linx.playAndroid.public.paging.PagingStateUtil

/**
 * 带刷新头的Card布局
 * LazyPagingItems<T>
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : Any> SwipeRefreshContent(
    modifier: Modifier = Modifier,
    lazyPagingListData: LazyPagingItems<T>,
    state: LazyListState = rememberLazyListState(),
    content: (LazyListScope.() -> Unit)? = null,
    cardHeight: Dp = 120.dp,
    itemContent: @Composable (index: Int, data: T) -> Unit
) {
    var refreshing by remember { mutableStateOf(false) }
    val updateRefreshStatus = { it: Boolean ->
        refreshing = it
    }

    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        //刷新数据
        lazyPagingListData.refresh()
    })
    val itemCount = lazyPagingListData.itemCount
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
            .then(modifier)
    ) {
        PagingStateUtil().PagingUtil(
            lazyPagingListData.loadState,
            itemCount,
            { lazyPagingListData.refresh() },
            refreshing,
            updateRefreshStatus,
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = state) {
                if (content != null) content()
                items(count = itemCount) { index ->
                    SimpleCard(cardHeight = cardHeight) {
                        val item = lazyPagingListData[index] ?: return@SimpleCard
                        itemContent(index, item)
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
                refreshState.isRefreshing = false
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