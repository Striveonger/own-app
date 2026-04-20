export class Dot {
    name: string;
    type: DotType;
    content?: string;
    imageUrl?: string;
}

export enum DotType {
    TEXT = "text",
    IMAGE = "image",
}
