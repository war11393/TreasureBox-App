package com.treasurebox.titwdj.treasurebox.Model.dbflow;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by 11393 on 2017/9/24.
 * DbFlow--基于AnnotationProcessing(注解处理器)的强大、健壮同时又简单的ORM框架
 */
@Database(name = TBdb.NAME, version = TBdb.VERSION)
public class TBdb {
    public static final String NAME = "TreasureBoxDatabase";
    public static final int VERSION = 6;
}
