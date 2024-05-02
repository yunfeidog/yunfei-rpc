package com.yunfei.rpc.spi;

/**
 * @author houyunfei
 */
public class Cat implements Animal{
    @Override
    public void eat(String food) {
        System.out.println("Cat eat "+ food);
    }
}
