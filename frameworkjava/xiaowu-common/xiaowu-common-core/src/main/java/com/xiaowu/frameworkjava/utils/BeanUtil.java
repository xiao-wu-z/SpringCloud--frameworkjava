package com.xiaowu.frameworkjava.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BeanUtil extends BeanUtils {

    /**
     * 集合数据拷贝
     * @param sourceList 源集合
     * @param targetSupplier 目标对象创建工厂
     * @return 目标集合
     * @param <S> 源类型
     * @param <T> 目标类型
     */
    public static <S, T> List<T> copyListProperties(List<S> sourceList, Supplier<T> targetSupplier) {
        List<T> targetList = new ArrayList<>();

        for (S source : sourceList) {
            T target = targetSupplier.get(); // 创建目标对象
            copyProperties(source, target); //单个对象拷贝
            targetList.add(target);
        }
        return targetList;
    }
}
