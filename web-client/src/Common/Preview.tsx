import React from "react";
import Resizer from "react-image-file-resizer";

export const resizeFile = (file: Blob): Promise<string> => {
    return new Promise((resolve) => {
        Resizer.imageFileResizer(
            file,
            100,
            500,
            'PNG',
            100,
            0,
            (uri) => {
                resolve(uri as string)
            },
            'base64'
        )
    })
}

interface Props {
    resizedFile: string
}

const Preview : React.FC<Props> = ({resizedFile}) => {
    return (
        <div>
            <img src={resizedFile} />
        </div>
    )
}

export default Preview