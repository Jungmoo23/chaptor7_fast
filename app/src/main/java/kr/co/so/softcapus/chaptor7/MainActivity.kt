package kr.co.so.softcapus.chaptor7

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button


class MainActivity : AppCompatActivity() {


    private val recordTimeTextview: CountUpView by lazy{
        findViewById(R.id.recordTimeTextview)
    }

    private val soundVisualizerView: SoundVisualizerView by lazy{
        findViewById(R.id.soundVisualizerView)
    }

    private val resetbtn: Button by lazy {
        findViewById(R.id.resetbtn)
    }

    private val recordBtn: RecordBtn by lazy {
        findViewById(R.id.recordbtn)
    }

    private val requiredPermission = arrayOf(Manifest.permission.RECORD_AUDIO)


    private val recordingFilpath: String by lazy {
        "${ externalCacheDir?.absolutePath}/recording.3gp"
    }
    private var player: MediaPlayer?=null
    private var recorder: MediaRecorder?=null
    private var state =State.BEFORE_RECORING
        set(value){
            field = value
            resetbtn.isEnabled = (value ==State.AFTER_RECORDING) || (value == State.ON_PLAYING)
            recordBtn.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermison()
        initView()
        bindViews()
        initVariables()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_PERMISSON
                &&  grantResults.firstOrNull() ==PackageManager.PERMISSION_GRANTED

        if(!audioRecordPermissionGranted){
            finish()
        }

    }

    private fun requestAudioPermison(){
        requestPermissions(requiredPermission,REQUEST_RECORD_AUDIO_PERMISSON)
    }


    private fun initView(){
        recordBtn.updateIconWithState(state)
    }

    private fun bindViews(){

        soundVisualizerView.onRequestCurrentAmplutude ={
            recorder?.maxAmplitude ?:0
        }


        resetbtn.setOnClickListener {
            stoplaying()
            state =State.BEFORE_RECORING
        }
        recordBtn.setOnClickListener {
            when (state){
                State.BEFORE_RECORING -> {
                    startRecord()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stoplaying()
                }

            }
        }
    }


    private fun startRecord() {
        recorder = MediaRecorder()
            .apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(recordingFilpath)
                prepare()
            }
        recorder?.start()
        soundVisualizerView.startVisualizing(false)
        recordTimeTextview.startCountUp()

        state = State.ON_RECORDING
    }


    private fun stopRecording(){
        recorder?.run{
            stop()
            release()
        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        state= State.AFTER_RECORDING
        recordTimeTextview.stopCountup()
    }

    private fun startPlaying(){
        player =MediaPlayer().apply {
            setDataSource(recordingFilpath)
            prepare()
        }
        soundVisualizerView.startVisualizing(true)
        player?.setOnCompletionListener {
            stoplaying()
            state = State.AFTER_RECORDING
        }
        player?.start()
        state = State.ON_PLAYING
        recordTimeTextview.startCountUp()


    }

    private fun stoplaying(){
        player?.release()
        player =null
        soundVisualizerView.stopVisualizing()

        state = State.AFTER_RECORDING
        recordTimeTextview.stopCountup()

    }

    private fun initVariables(){
        state =State.BEFORE_RECORING
    }

    companion object{
        private const val REQUEST_RECORD_AUDIO_PERMISSON = 201
    }


}