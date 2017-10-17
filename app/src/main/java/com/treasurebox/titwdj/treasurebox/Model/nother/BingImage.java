package com.treasurebox.titwdj.treasurebox.Model.nother;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 11393 on 2017/8/13.
 */
public class BingImage {
    @SerializedName("images")
    public List<Images> imageList;

    public class Images {
        @SerializedName("url")
        public String imageurl;
        @SerializedName("enddate")
        public String enddate;
    }
}
