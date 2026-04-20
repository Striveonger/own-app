<template>
    <div>
        <!-- <HelloWorld name="Vue 3" /> -->
        <AsyncComponent :dot="dot" />
    </div>
</template>

<script setup lang="ts">
import { defineAsyncComponent, ref, Ref } from "vue";
import { Dot, DotType } from "@/entity/Dot";

const dot: Ref<Dot> = ref({
    name: "dot",
    type: DotType.TEXT,
    content: "dot content",
    imageUrl: "https://picsum.photos/200/300",
});

// 动态加载Hello组件
dot.value.name = "Tom";
const AsyncComponent = ref(defineAsyncComponent(() => import("./Text.vue")));

// Delay for 5 seconds before changing the AsyncComponent
setTimeout(() => {
    dot.value.name = "Jerry";
    dot.value.type = DotType.IMAGE;
    AsyncComponent.value = defineAsyncComponent(() => import("./Image.vue"));
}, 5000);
</script>

<style scoped lang="scss">

</style>
