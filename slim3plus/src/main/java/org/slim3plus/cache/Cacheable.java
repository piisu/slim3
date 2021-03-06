package org.slim3plus.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cacheable {
    Class<? extends LowLevelCache>[] value();

    String version() default "default";

    /***
     * キャッシュの有効期限(秒)
     * 負の値を設定した場合は、無期限でキャッシュする
     * @return キャッシュの有効期限
     */
    int expiration() default 7 * 24 * 60 * 60;

}
