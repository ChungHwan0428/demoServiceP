package com.practice.demo.factory.entity;

import com.practice.demo.entity.post.Image;

public class ImageFactory {
    public static Image createImage(){
        return new Image("origin_filename.jpg");
    }

    public static Image createImageWithOriginName(String originName){
        return new Image(originName);
    }

    public static Image createImageWithIdAndOriginName(Long id, String originName){
        return new Image(id,originName);
    }
}
