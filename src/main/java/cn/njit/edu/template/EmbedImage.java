package cn.njit.edu.template;

import com.deepoove.poi.data.PictureRenderData;

import java.io.File;

public class EmbedImage extends PictureRenderData {


    public EmbedImage(int width, int height, File picture) {
        super(width, height, picture);
    }
    public String getPath() {
        return super.getPath().toLowerCase();
    }
}
