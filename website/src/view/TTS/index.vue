<template>
    <div class="container">
        <h2>TTS - Demo</h2>
        <el-row>
            <!-- 输入框 -->
            <el-input v-model="text" type="textarea" :rows="5" placeholder="请输入文字"/>
        </el-row>
        <el-row>
            <el-col :span="5">
                <!-- 选择声音 -->
                <el-select v-model="voice" placeholder="声音" size="large">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" style="padding-left: 15px"/>
                </el-select>
            </el-col>
            <el-col :span="6">
                <!-- 语速 -->
                <el-slider v-model="speed" :format-tooltip="speedFormatTooltip" size="large" :step="4" :min="20"/>
            </el-col>
            <el-col :span="1">
                <!-- 播放 -->
                <el-button :icon="VideoPlay" circle @click="submitTrigger"/>
            </el-col>
        </el-row>
    </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import {VideoPlay} from '@element-plus/icons-vue'
import {AudioGenerateDTO, submit} from "@/view/TTS/index.ts";

const text = ref('你好呀~');

const voice = ref('');

const options = [
    {
        value: 'alex',
        label: 'alex',
    },
    {
        value: 'anna',
        label: 'anna',
    },
    {
        value: 'bella',
        label: 'bella',
    },
    {
        value: 'benjamin',
        label: 'benjamin',
    },
    {
        value: 'charles',
        label: 'charles',
    },
    {
        value: 'claire',
        label: 'claire',
    },
    {
        value: 'david',
        label: 'david',
    },
    {
        value: 'diana',
        label: 'diana',
    }
];

const speed = ref(40);

const submitTrigger = () => {
    let audio = new AudioGenerateDTO();
    audio.text = text.value;
    audio.voice = voice.value;
    audio.speed = speedFormatTooltip(speed.value);
    submit(audio).then(key => {
        // 创建播放音频
        let url = `/api/v1/audio/play?key=${key}`;
        console.log('audio play url: ', url);
        let audio = new Audio(url);
        audio.play();
    });
};

const speedFormatTooltip = (val: number) => {
    return val / 40;
}

</script>

<style scoped lang="scss">
* {
    margin: 0;
    padding: 0;
}

h2 {
    margin-bottom: 15px;
    padding-left: 10px;
    border-bottom: 1px solid #cccccc; /* 添加黑色下边框 */
    padding-bottom: 10px; /* 添加下内边距，让文字与边框有间距 */
}

.el-row {
    margin-bottom: 20px;
    margin-left: 10px;
    margin-right: 10px;
}

.el-col {
    margin-right: 10px;
    display: inline-block;
}

.container {
    max-width: 650px;
    min-width: 570px;
    margin: 0 auto; /* 水平居中 */
    padding: 20px;  /* 添加一些内边距，让内容不贴边 */
}
</style>