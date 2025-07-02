<template>
    <!-- <router-view /> -->
    <div>
        <!-- <DynamicLoadComponent /> -->
        <!-- <LazyLoadingImages /> -->
        <TTS />

    </div>
</template>

<script setup lang="ts">
// 动态加载组件 --- 示例
import DynamicLoadComponent from '@/view/DynamicLoadComponent/index.vue';
// 图片懒加载
import LazyLoadingImages from '@/view/LazyLoadingImages/index.vue';

// 文字转语音
import TTS from '@/view/TTS/index.vue';

</script>

<style scoped>
/* 如果需要，可以在这里添加全局样式 */

</style>
