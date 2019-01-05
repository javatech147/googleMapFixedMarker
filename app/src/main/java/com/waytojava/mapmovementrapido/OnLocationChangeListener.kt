package com.waytojava.mapmovementrapido

import android.location.Location

/**
 * Created by Manish Kumar on 1/5/2019
 */
interface OnLocationChangeListener {
    fun locationChange(location: Location?)
}