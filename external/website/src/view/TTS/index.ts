import {http, Result} from "@/api";

class SubmitResult {
    key: string;
}

export class AudioGenerateDTO {
    text: string;
    voice: string;
    speed: number;
}

export async function submit(audio: AudioGenerateDTO): Promise<string> {
    console.log('submit', audio);
    let result = await http.post('/api/v1/audio/submit', audio)
        .then(response => {
            console.log("audio submit result: ", response);
            return response.data as Result<SubmitResult>;
        });
    // 阻塞一下当前线程
    // await new Promise(resolve => setTimeout(resolve, 1000));
    let data = result.data as SubmitResult;
    return data.key;
}
