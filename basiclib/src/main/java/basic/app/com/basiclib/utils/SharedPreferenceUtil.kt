package basic.app.com.basiclib.utils

import android.content.Context
import android.content.SharedPreferences
import java.lang.IllegalArgumentException

/**
 * author : user_zf
 * date : 2018/8/31
 * desc : SharedPreferences扩展方法，对Context和Fragment做扩展
 */

const val FILE_NAME_USER_INFO = "user_info"  //用来保存用户信息
const val FILE_NAME_COMMON = "common_info"   //保存应用配置信息
const val FILE_NAME_DEFAULT = FILE_NAME_COMMON
/**
 * 向文件名为fileName的SharedPreference插入数据
 */
fun android.content.Context.putSharedPreferencesValue(key: String, value: Any?, fileName: String = FILE_NAME_DEFAULT) {
    val sp: SharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)
    when (value) {
        is Int? -> sp.edit().putInt(key, value as Int).apply()
        is Long? -> sp.edit().putLong(key, value as Long).apply()
        is Float? -> sp.edit().putFloat(key, value as Float).apply()
        is Boolean? -> sp.edit().putBoolean(key, value as Boolean).apply()
        is String? -> sp.edit().putString(key, value as String).apply()
        else -> {
            throw IllegalArgumentException()
        }
    }
}

/**
 * 从文件名为fileName的SharedPreference中获得数据
 */
fun android.content.Context.getSharedPreferencesValue(
        key: String, clz: Class<out Any?>, defValue: Any? = null, fileName: String = FILE_NAME_DEFAULT): Any? {

    val sp: SharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)
    return when (clz) {
        java.lang.Integer::class.java -> sp.getInt(key, (defValue ?: 0) as Int)
        Int::class.java -> sp.getInt(key, (defValue ?: 0) as Int)
        java.lang.Long::class.java -> {
            val value = if (defValue is Int) (defValue as? Int)?.toLong() else (defValue as? Long)
            sp.getLong(key, value ?: 0L)
        }
        Long::class.java -> sp.getLong(key, (defValue ?: 0L) as Long)
        java.lang.Float::class.java -> sp.getFloat(key, (defValue ?: 0f) as Float)
        Float::class.java -> sp.getFloat(key, (defValue ?: 0f) as Float)
        java.lang.Boolean::class.java -> sp.getBoolean(key, (defValue ?: false) as Boolean)
        Boolean::class.java -> sp.getBoolean(key, (defValue ?: false) as Boolean)
        java.lang.String::class.java -> sp.getString(key, (defValue ?: "") as String)
        String::class.java -> sp.getString(key, (defValue ?: "") as String)
        else -> {
            throw IllegalArgumentException()
        }
    }
}

/**
 * 从文件名为fileName的Sharedpreference中获得数据
 */
inline fun <reified V : Any?> android.content.Context.getSharedPreferencesValue(
        key: String, defValue: V? = null, fileName: String = FILE_NAME_DEFAULT): V? {
    return getSharedPreferencesValue(key, V::class.java, defValue, fileName) as? V
}

/**
 * 清空文件名为fileName的SharedPreference的所有数据
 */
fun android.content.Context.clearSharePreferencesValues(fileName: String = FILE_NAME_DEFAULT) {
    getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().clear().apply()
}

/**
 * 从文件名为fileName中移除对应key的数据
 */
fun android.content.Context.removeSharedPreferencesValue(key: String, fileName: String = FILE_NAME_DEFAULT) {
    getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().remove(key).apply()
}


/**
 * 向文件名为fileName的SharedPreference插入数据
 */
fun android.support.v4.app.Fragment.putSharedPreferencesValue(key: String, value: Any?, fileName: String = FILE_NAME_DEFAULT) {
    context?.putSharedPreferencesValue(key, value, fileName)
}

/**
 * 向文件名为fileName的SharedPreference插入数据
 */
fun android.support.v4.app.Fragment.getSharedPreferencesValue(
        key: String, clz: Class<out Any?>, defValue: Any? = null, fileName: String = FILE_NAME_DEFAULT): Any? {
    return context?.getSharedPreferencesValue(key, clz, defValue, fileName)
}

/**
 * 向文件名为fileName的SharedPreference插入数据
 */
inline fun <reified V : Any?> android.support.v4.app.Fragment.getSharedPreferencesValue(
        key: String, defValue: V? = null, fileName: String = FILE_NAME_DEFAULT): V? {
    return context?.getSharedPreferencesValue<V>(key, defValue, fileName)
}

/**
 * 清空文件名为fileName的Sharedpreference的所有数据
 */
fun android.support.v4.app.Fragment.clearSharePreferencesValues(fileName: String = FILE_NAME_DEFAULT) {
    context?.clearSharePreferencesValues(fileName)
}

/**
 * 从文件名为fileName中移除对应key的数据
 */
fun android.support.v4.app.Fragment.removeSharedPreferencesValue(key: String, fileName: String = FILE_NAME_DEFAULT) {
    removeSharedPreferencesValue(key, fileName)
}