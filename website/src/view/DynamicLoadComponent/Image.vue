<template>
    <h1>Hello {{ dot.name }}</h1>
    <img
        v-if="dot.type === DotType.IMAGE"
        :src="dot.imageUrl"
        alt="dot image"
    />
</template>
<script setup lang="ts">
import { Dot, DotType } from "@/entity/Dot";

// 定义页面组件的入参() props 接收参数
defineProps<{ dot: Dot }>();
</script>
<style scoped>
h1 {
    color: rgba(180, 53, 219, 0.609);
    font-size: 34px;
    font-weight: bold;
    text-align: center;
    margin-top: 50px;
    margin-bottom: 50px;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

img {
    display: block;
    margin-left: auto;
    margin-right: auto;
    width: 200px;
    height: 300px;
    object-fit: cover;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    margin-top: 20px;
    margin-bottom: 20px;
    padding: 20px;
}
</style>
