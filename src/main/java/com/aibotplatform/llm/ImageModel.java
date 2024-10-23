package com.aibotplatform.llm;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.image.Text2ImageResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.UUID;

import static com.baidubce.qianfan.core.auth.Auth.TYPE_OAUTH;

public class ImageModel implements LLM{
    private static final String ak = "uMF5PVIQDQYY58QZJ0J04XrF";
    private static final String sk = "zzNMgEl8pDpDBEQLVpawuQLRzRnYkVh1";
    private static final String imageFolder = "src/main/resources/static/BotImage";
    private final Qianfan qianfan = new Qianfan(TYPE_OAUTH,ak, sk);

    /**
     * 输入提示词，输出本地图片名称（地址）
     * @param prompt
     * @return String
     */
    @Override
    public String chat(String prompt) {
        Text2ImageResponse response = qianfan.text2Image().model("Stable-Diffusion-XL")
                .prompt(prompt)
                .execute();
        byte[] image_data = response.getData().get(0).getImage();
        return saveImage(image_data, imageFolder);
    }

    public static String saveImage(byte[] imageData, String folderPath) {
        String fileName = UUID.randomUUID() + ".png"; // 生成唯一文件名
        File outputFile = new File(folderPath, fileName); // 在指定文件夹中创建文件
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
            ImageIO.write(img, "png", outputFile); // 保存图像
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputFile.getName();
    }
}
