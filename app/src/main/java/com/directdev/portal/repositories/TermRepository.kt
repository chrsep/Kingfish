package com.directdev.portal.repositories

import com.directdev.portal.models.TermModel
import io.realm.Realm
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/29/17.
 *------------------------------------------------------------------------------------------------*/
class TermRepository @Inject constructor() {
    fun getTerms(): List<Int> = Realm.getDefaultInstance().use {
        it.where(TermModel::class.java)
                .findAllSorted("value")
                .map { term -> term.value }
    }

    fun save(terms: List<TermModel>) = Realm.getDefaultInstance().use {
        it.executeTransaction {
            it.insertOrUpdate(terms)
        }
    }

    fun getLatestTerm(): Int = Realm.getDefaultInstance().use {
        it.where(TermModel::class.java)
                .findAllSorted("value")
                .map { it.value }
                .last()
    }

    fun getFirstTerm(): Int = Realm.getDefaultInstance().use {
        it.where(TermModel::class.java)
                .findAllSorted("value")
                .map { it.value }
                .first()
    }


}
