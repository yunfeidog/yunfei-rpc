package com.yunfei.rpc.spi;

/**
 * @author houyunfei
 */
public class Dog implements Animal {
    @Override
    public void eat(String food) {
        System.out.println("Dog eat " + food);
    }
}
