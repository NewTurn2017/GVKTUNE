package com.gvkorea.gvktune.view.view.rt.util


interface ClapAnalyzer : AudioClipListener {
  fun getBaseLineV():Double
  fun getBaseLine():Double
  val isListeningBase: Boolean
  val data: ShortArray
  val status: StatusUpdate
  val results: Results
  var cancel: Boolean
  fun dispHistory()
  fun clap_heard(): Boolean
  fun done(): Boolean
  fun process()
}