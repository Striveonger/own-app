<template>
    <div class="main-container">
        <div v-for="item in items" :key="item.name">
            <img class="lazy" src="" :alt="item.content" :data-src="item.imageUrl" />
            <span>{{item.name}}</span>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, Ref, nextTick } from "vue";
import { Dot, DotType } from "@/entity/Dot";

// 初始化数据
const items: Ref<Array<Dot>> = ref([]);
for (let i = 1; i <= 1000; i++) {
    items.value.push({
        name: `dot-${i}`,
        type: DotType.IMAGE,
        content: `dot-content-${i}`,
        imageUrl: `https://picsum.photos/200/300?random=${i}`,
    });
}

nextTick(() => {
    // 开始设置懒加载
    const images = document.querySelectorAll('.lazy');
    
    // 定义一个观察器
    const callback = (entries) => {
        // 遍历所有被观察的元素
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                // 获取当前元素
                const img = entry.target as HTMLImageElement;
                // 将元素的 src 属性设置为 data-src 属性的值
                img.src = img.dataset.src as string;
                console.log('图片进入视口: url: {}', img.src);
                // 停止观察当前元素
                observer.unobserve(img);
            }
        });
    };
    const options = {
        root: null,
        rootMargin: '500px',
        threshold: 0,
    };
    const observer = new IntersectionObserver(callback, options);
    // 开始观察所有图片
    images.forEach(image => {
        observer.observe(image);
    });
});
</script>

<style scoped lang="scss">
img {
    width: 200px;
    height: 300px;
    border: 1px solid black;
}
.main-container {
    width: 100%;
    height: 100%;
    padding-left: 20px;
    padding-right: 20px;
    padding-top: 50px;
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    grid-gap: 10px;
}
</style>
