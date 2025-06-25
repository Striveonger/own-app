<template>
    <H1>Hello {{ dot.name }}</H1>
    <v-html v-if="dot.type === DotType.TEXT" class="text-content">
        {{ dot.content }}
    </v-html>
</template>

<script setup lang="ts">
import { Dot, DotType } from "@/entity/Dot";

// 定义页面组件的入参() props 接收参数
defineProps<{ dot: Dot }>();
</script>

<style scoped>
h1 {
    color: red;
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

.text-content {
    font-size: 18px;
    color: #333;
    padding: 20px;
    line-height: 1.6;
    margin-top: 20px;
}
</style>
