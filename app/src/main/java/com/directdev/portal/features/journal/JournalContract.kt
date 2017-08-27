package com.directdev.portal.features.journal

import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/26/17.
 *------------------------------------------------------------------------------------------------*/
interface JournalContract {
    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter
}