package com.directdev.portal.repositories

import android.content.Context
import com.directdev.portal.utils.clearPref
import io.realm.Realm
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/25/17.
 *------------------------------------------------------------------------------------------------*/
class MainRepository @Inject constructor(val ctx: Context, val realm: Realm) {

    fun cleanData() {
        if (!realm.isEmpty) realm.executeTransactionAsync {
            it.deleteAll()
        }
        realm.close()
        ctx.clearPref()
    }
}