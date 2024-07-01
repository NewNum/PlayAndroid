package com.linx.playAndroid.public.paging

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.linx.playAndroid.public.ErrorComposable

class PagingStateUtil {

    //是否显示无数据的布局
    var showNullScreen = false

    /**
     * 统一管理Paging数据状态的方法
     * 错误处理、加载中的显示方式
     */
    @Composable
    fun PagingUtil(
        //paging数据
        loadState: CombinedLoadStates,
        itemCount: Int,
        refresh: () -> Unit,
        //刷新状态
        refreshState: Boolean,
        updateRefreshState: (Boolean) -> Unit,
        content: @Composable () -> Unit
    ) {

        when (loadState.refresh) {
            //未加载且未观察到错误
            is LoadState.NotLoading -> NotLoading(updateRefreshState) {
                //允许显示无数据布局 这里通常是第一次获取数据
                when (itemCount) {
                    0 -> {
                        //第一次进入允许显示空布局
                        if (!showNullScreen) showNullScreen = true
                        //显示无数据布局
                        else
                            ErrorComposable("暂无数据，请点击重试") {
                                refresh()
                            }
                    }

                    else -> content()
                }
            }
            //加载失败
            is LoadState.Error -> Error(refresh, updateRefreshState)
            //加载中
            LoadState.Loading -> Loading(refreshState, updateRefreshState)
        }

        //如果在加载途中遇到错误的话，pagingData的状态为append
        when (loadState.append) {
            //加载失败
            is LoadState.Error -> Error(refresh, updateRefreshState)
            //加载中
            LoadState.Loading -> Loading(refreshState, updateRefreshState)
            //未加载且未观察到错误
            is LoadState.NotLoading -> NotLoading(updateRefreshState) {
                //允许显示无数据布局 这里通常是第一次获取数据
                when (itemCount) {
                    0 -> {
                        //第一次进入允许显示空布局
                        if (!showNullScreen) showNullScreen = true
                        //显示无数据布局
                        else
                            ErrorComposable("暂无数据，请点击重试") {
                                refresh()
                            }
                    }

                    else -> content()
                }
            }

            else -> {}
        }

    }

    /**
     * 未加载且未观察到错误
     */
    @Composable
    private fun NotLoading(
        updateRefreshState: (Boolean) -> Unit,
        content: @Composable () -> Unit
    ) {

        content()

        //让刷新头停留一下子再收回去
        updateRefreshState.invoke(false)
    }

    /**
     * 加载失败
     */
    @Composable
    private fun  Error(
        refresh: () -> Unit,
        updateRefreshState: (Boolean) -> Unit,
    ) {
        updateRefreshState.invoke(false)
        ErrorComposable {
            refresh()
        }
    }

    /**
     * 加载中
     */
    @Composable
    private fun Loading(
        refreshState: Boolean,
        updateRefreshState: (Boolean) -> Unit,
    ) {
        Row(modifier = Modifier.fillMaxSize()) { }
        //显示刷新头
        if (!refreshState) updateRefreshState.invoke(true)
    }

}


@Composable
fun <T : Any> SwipeRefreshList(
    collectAsLazyPagingItems: LazyPagingItems<T>,
    content: LazyListScope.() -> Unit
) {
    val rememberSwipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)
    SwipeRefresh(
        state = rememberSwipeRefreshState,
        onRefresh = { collectAsLazyPagingItems.refresh() }) {

        rememberSwipeRefreshState.isRefreshing =
            collectAsLazyPagingItems.loadState.refresh is LoadState.Loading

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            content()

            collectAsLazyPagingItems.apply {
                when {
                    loadState.append is LoadState.Loading -> {//加载更多时，就在底部显示loading的item
                        item { LoadingItem() }
                    }

                    loadState.append is LoadState.Error -> {//加载更多的时候出错了，就在底部显示错误的item
                        item {
                            ErrorItem() {
                                collectAsLazyPagingItems.retry()
                            }
                        }
                    }

                    loadState.refresh is LoadState.Error -> {
                        if (collectAsLazyPagingItems.itemCount <= 0) {//刷新的时候，如果itemCount小于0，说明是第一次进来，出错了显示一个大的错误内容
                            item {
                                ErrorContent() {
                                    collectAsLazyPagingItems.retry()
                                }
                            }
                        } else {
                            item {
                                ErrorItem() {
                                    collectAsLazyPagingItems.retry()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ErrorItem(retry: () -> Unit) {
    Button(onClick = { retry() }, modifier = Modifier.padding(10.dp)) {
        Text(text = "重试")
    }
}

@Composable
fun ErrorContent(retry: () -> Unit) {
    Text(text = "请求出错啦")
    Button(onClick = { retry() }, modifier = Modifier.padding(10.dp)) {
        Text(text = "重试")
    }
}

@Composable
fun LoadingItem() {
    CircularProgressIndicator(modifier = Modifier.padding(10.dp))
}
