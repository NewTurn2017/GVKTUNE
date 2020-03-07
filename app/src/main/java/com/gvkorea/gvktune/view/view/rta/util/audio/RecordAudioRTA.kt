package com.gvkorea.gvktune.view.view.rta.util.audio

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.util.Log
import com.gvkorea.gvktune.util.fft.RealDoubleFFT
import com.gvkorea.gvktune.view.view.rta.RtaFragment
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.AUTODETECCION
import com.gvkorea.gvktune.view.view.rta.RtaFragment.Companion.isStartedAudio
import com.gvkorea.gvktune.view.view.rta.util.Maximum
import kotlinx.android.synthetic.main.fragment_rta.*
import kotlin.math.*

class RecordAudioRTA(val view: RtaFragment) : AsyncTask<Unit, ShortArray, Unit>() {

    private val frequency = 8000
    private val channelConfiguration = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT

    lateinit var transformer: RealDoubleFFT
    var bufferReadResult = 0

    var MIN_FREQUENCY = 50.0 // HZ

    var MAX_FREQUENCY = 3000.0 // HZ


    lateinit var audioRecord: AudioRecord
    var freq_value = 0.0
    var frec_ref = 440.0

    // Array con la scale cromatica
    var scale = arrayOf("F#", "G", "G#", "A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F")


    // * Aqui tendremos encuenta el rango audible por una posible reutilizacion del codigo
    // aunque en la practica solo estudiemos el espectro en un rango menor, de 50 a 4000 Hz p.ej.
    var n = 66 // indice correspondiente al maximo frecuencial: 440*2^(66/12) = 19912.127 Hz

    var g = -51.0
    var j: Double = 0.0
    var fin = n + abs(g).toInt() // numero de posiciones desde 'j' hasta 'n'


    // Array con las notas asociadas al array de frecuencias
    lateinit var a: String //variable de tipo cadena de caracteres para la nota
    lateinit var G: Array<String> // conjunto de notas posibles
    val aux3: DoubleArray = DoubleArray(view.FRAMELENGTH)
    // declaracion de vector auxiliar para el estudio de la trama
    // sera el array que contenga la amplitud de los armonicos

    var REL_AMP =
        8 // relacion de amplitudes que han de tener los dos primeros armonicos para hallar la nota

    var REL_FREC =
        4 // relacion en frecuencia que han de tener los dos primeros armonicos para hallar la nota


    override fun doInBackground(vararg p0: Unit?): Unit? {
        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                frequency,
                channelConfiguration, audioEncoding
            )

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION, frequency,
                channelConfiguration, audioEncoding, bufferSize
            )

            val buffer = ShortArray(view.blockSize_buffer)
            audioRecord.startRecording()

            while (isStartedAudio) {
                bufferReadResult = audioRecord.read(buffer, 0, view.blockSize_buffer)

                publishProgress(buffer)
            }
            audioRecord.stop()
        } catch (t: Throwable) {
            Log.e("AudioRecord", "Recording Failed")
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressUpdate(vararg toTransform: ShortArray) {

        var maximum: Double = 0.0
        var variance: Double = 0.0
        var plot = DoubleArray(view.blockSize_fft)
        val plotSpectrum: DoubleArray
        transformer = RealDoubleFFT(view.blockSize_fft)

        for (i in 0 until bufferReadResult) {
            plot[i * 2] = toTransform[0][i].toDouble()
            plot[i * 2 + 1] = 0.0
        }

        maximum = max(plot, 0, plot.size).value

        plot = normalize(plot)

        variance = calculateVariance(plot)

        if (AUTODETECCION) {
            if (view.valid[1] != 0.0) { // Si han aprecido armonicos
                if ((maximum >= 800) && (variance > 0.04)) {
                    isStartedAudio = false;
                    view.startStopButton.text = "ON";
                    this.cancel(true)
                }
            }
        }

        plot = applyHamming(plot)

        transformer.ft(plot)

        view.StatusTextView.textSize = view.TEXT_SIZE.toFloat()
//        returnNote(plot)

        if (freq_value > MIN_FREQUENCY) {
            val position: Int = calculateIndex(freq_value)
            view.StatusTextView.text =
                searchNote(position) + " (" + view.df1.format(freq_value) + " Hz)"
        }

        view.drawAxisFrequencies()

        plotSpectrum = normalize(plot)
        drawSpectrum(plotSpectrum)

        view.canvas6.drawLine(
            0f,
            view.chartHeight - view.threshold_height,
            view.blockSize_graph,
            view.chartHeight - view.threshold_height,
            view.paint6
        )

        writeHarmonics()
    }

    private fun writeHarmonics() {
        view.paint4.isAntiAlias = true
        view.paint4.isFilterBitmap = true
        view.paint4.textSize = view.TEXT_SIZE2.toFloat()




        for (num in 0 until view.NUM_harmonics) {
            if (view.valid[num] != 0.0) {
                view.canvas4.drawText(
                    view.df1.format(view.valid[num]) + "[" + searchNote(
                        calculateIndex(
                            view.valid[num]
                        )
                    ) + "]", 120 * num.toFloat(), 25f, view.paint4
                )
            }
        }
    }

    private fun searchNote(index: Int): String {
        return G[index]
    }

    private fun calculateIndex(pitch: Double): Int {
        val num: Double // indice correspondiente a la posicion respecto al LA4

        // (sera el valor que redondearemos para obtener 'indice')

        // (sera el valor que redondearemos para obtener 'indice')
        val index: Int // valor redondeado de num

        // La siguiente operacion devuelve el indice correspondiente a la frecuencia
        // detectada, es la operacion inversa a la utilizada para calcular la teorica

        // La siguiente operacion devuelve el indice correspondiente a la frecuencia
        // detectada, es la operacion inversa a la utilizada para calcular la teorica
        num = 12 * log10(pitch / frec_ref) / log10(2.0) + 51

        index = num.roundToInt().toInt() // convierte el indice a entero
        return index
    }

    private fun returnNote(plot: DoubleArray): String {
        freq_value = 0.0
        val note_final: String
        val harmonics = returnHarmonics(plot)
        val notes =
            arrayOfNulls<String>(view.NUM_harmonics) // vector con las notas correspondientes a los armonicos validos

        // vector de numeros enteros con la correspondecia a la posicion dentro del array "scale" con las notas
        // de los armonicos validos
        val indices = IntArray(view.NUM_harmonics)

        for (i in 0 until view.NUM_harmonics) {
            view.valid[i] = 0.0
        }

        var m = 0
        var n = 0

        while ((m < view.NUM_harmonics) && (n < harmonics.size - 1)) {
            // Evitamos que se repita mas de una vez un mismo armonico y que aparezcan dos muy proximos
            // Desventaja: puede que de dos muy proximos no tomemos el de mayor amplitud

            // Si lo que viene luego en la posicion correspondiente al vector armonicos es distinto a lo que hay ahora


            // Evitamos que se repita mas de una vez un mismo armonico y que aparezcan dos muy proximos
            // Desventaja: puede que de dos muy proximos no tomemos el de mayor amplitud


            // Si lo que viene luego en la posicion correspondiente al vector armonicos es distinto a lo que hay ahora
            if (harmonics[n + 1] != harmonics[n]) {

                // Si la diferencia de distancia en frecuencia entre lo que viene luego y lo que tengo ahora
                // es menor que la LONGITUD de TRAMA mejor quedate con lo que viene luego
                //if(Math.abs(armonicos[n+1]-armonicos[n])<LONGTRAMA/2){
                if (abs(harmonics[n + 1] - harmonics[n]) < view.FRAMELENGTH) {
                    view.valid[m] = harmonics[n + 1]
                    view.amplitudes[m] = aux3[n + 1]
                    notes[m] =
                        searchNote(calculateIndex(harmonics[n + 1])) // calcula la nota en funcion de la frecuencia

                    // devuelve el indice correspondiente a la posicion que ocupa la nota en el array "scale"
                    indices[m] = returnPosition(searchNote(calculateIndex(harmonics[n + 1])))
                    m += 1 // avanzamos una posicion en "validos"
                    n += 2 // avanzamos dos en "armonicos"
                } else {
                    view.valid[m] = harmonics[n]
                    view.amplitudes[m] = aux3[n]
                    notes[m] =
                        searchNote(calculateIndex(harmonics[n])) // calcula la nota en funcion de la frecuencia

                    // devuelve el indice correspondiente a la posicion que ocupa la nota en el array "scale"
                    indices[m] = returnPosition(searchNote(calculateIndex(harmonics[n])))
                    m++
                    n++
                }
            } else {
                n++
            }
        }

        val Min: Double
        val Max: Double
        val Higher: Double
        val Less: Double
        val relation_amp: Double
        val relation_freq: Double

        if (view.amplitudes[1] > view.amplitudes[0]) {
            Min = view.amplitudes[0]
            Max = view.amplitudes[1]
        } else {
            Min = view.amplitudes[1]
            Max = view.amplitudes[0]
        }

        if (view.valid[1] > view.valid[0]) {
            Less = view.valid[0]
            Higher = view.valid[1]
        } else {
            Less = view.valid[1]
            Higher = view.valid[0]
        }

        relation_amp = Max / Min
        relation_freq = Higher / Less

        if ((relation_amp > REL_AMP) && (relation_freq > REL_FREC)) {
            freq_value = returnPitch(plot)
            note_final = searchNote(calculateIndex(freq_value))
        } else {
            // La diferencia entre los indices de ambos armonicos siempre ha de guardar
            // una cantidad de 3 unidades
            // Comprobamos que validos[1]!=0,es decir no hay armonico, para no confundir la nota
            // Fa# (scale[0]) presente en acordes como B = 5+9+0 ó D = 8+3+0


            // La diferencia entre los indices de ambos armonicos siempre ha de guardar
            // una cantidad de 3 unidades
            // Comprobamos que validos[1]!=0,es decir no hay armonico, para no confundir la nota
            // Fa# (scale[0]) presente en acordes como B = 5+9+0 ó D = 8+3+0
            if (indices[1] >= 0 && Math.abs(indices[0] - indices[1]) >= 3 && view.valid[1] != 0.0) {
                val less: Int
                val higher: Int //

                // comprueba que indice es menor y cual mayor
                // para pasarselos como entrada al algoritmo
                // que estima la nota en funcion de las componentes
                if (indices[1] > indices[0]) {
                    less = indices[0]
                    higher = indices[1]
                } else {
                    less = indices[1]
                    higher = indices[0]
                }

                // Tenemos dos indices de armonicos que guardan una distancia suficiente para formar una nota
                note_final = determineNote(less, higher, indices[0])
                /// Determinar freq_asociada
                // Si la nota esta en el acorde
                if (note_final === scale.get(indices.get(0))) {
                    freq_value = view.valid.get(1) / 3
                } else if (note_final === scale.get(indices.get(1))) {
                    freq_value = view.valid.get(0) / 3
                } else {
                    freq_value = view.valid.get(0) / 3
                }
                // coge la frecuencia de la otra nota y la divide entre 3
                // Si no coge validos[0] y lo divide entre 3
            } else if (indices[0] - indices[1] == 0) { // si tenemos dos veces la misma nota
                note_final = notes[0]!! // sera esta la que prevalezca
                freq_value = view.valid[0]
            } else { // si no cumple ninguno de estos requisitos suponemos que es la de mayor amplitud
                freq_value = returnPitch(plot)
                note_final = searchNote(calculateIndex(freq_value))

                //nota_final = DevuelveNota(CalculaIndice(devuelvePitch(trama)));
            }


        }

        return note_final


    }

    private fun determineNote(less: Int, higher: Int, default: Int): String {
        // scale cromatica y los valores numericos de las notas como acordes
        /* F# = [0,4,7]  = [F#,Bb,C#]
    	 * G  = [1,5,8]  = [G,B,D]
    	 * G# = [2,6,9]  = [G#,C,Eb]
    	 * A  = [3,7,10] = [A,C#,E]
    	 * Bb = [4,8,11] = [Bb,D,F]
    	 * B  = [5,9,0]  = [B,Eb,F#]
    	 * C  = [6,10,1] = [C,E,G]
    	 * C# = [7,11,2] = [C#,F,G#]
    	 * D  = [8,0,3]  = [D,F#,A]
    	 * Eb = [9,1,4]  = [Eb,G,Bb]
    	 * E  = [10,2,5] = [E,G#,B]
    	 * F  = [11,3,6] = [F,A,C] */

        // Cadena que devolvera como nota estimada


        // scale cromatica y los valores numericos de las notas como acordes
        /* F# = [0,4,7]  = [F#,Bb,C#]
    	 * G  = [1,5,8]  = [G,B,D]
    	 * G# = [2,6,9]  = [G#,C,Eb]
    	 * A  = [3,7,10] = [A,C#,E]
    	 * Bb = [4,8,11] = [Bb,D,F]
    	 * B  = [5,9,0]  = [B,Eb,F#]
    	 * C  = [6,10,1] = [C,E,G]
    	 * C# = [7,11,2] = [C#,F,G#]
    	 * D  = [8,0,3]  = [D,F#,A]
    	 * Eb = [9,1,4]  = [Eb,G,Bb]
    	 * E  = [10,2,5] = [E,G#,B]
    	 * F  = [11,3,6] = [F,A,C] */

        // Cadena que devolvera como nota estimada
        var note: String =
            scale[default] // por defecto es la que esta primera en el array de armonicos


        // Iremos descartando posibilidades teniendo ordenados de menor a mayor los indices
        // Empezamos por el menor que es el 0, conforme mayor sea el indice menor, menos probabilidades
        // habra de que guarde una relacion de 3 unidades con el mayor indice


        // Iremos descartando posibilidades teniendo ordenados de menor a mayor los indices
        // Empezamos por el menor que es el 0, conforme mayor sea el indice menor, menos probabilidades
        // habra de que guarde una relacion de 3 unidades con el mayor indice
        if (less == 0) {
            if (higher == 4 || higher == 7) {
                note = scale.get(0) // es LA
            } else if (higher == 5 || higher == 9) {
                note = scale.get(5)
            } else if (higher == 3 || higher == 8) {
                note = scale.get(8) // es RE
            }
        } else if (less == 1) {
            if (higher == 5 || higher == 8) {
                note = scale.get(1) // es SOL
            } else if (higher == 6 || higher == 10) {
                note = scale.get(6)
            } else if (higher == 4 || higher == 9) {
                note = scale.get(9) // es MIb

                //freq_asociada = validos[0]/3;
            }
        } else if (less == 2) {
            if (higher == 6 || higher == 9) {
                note = scale.get(2) // es 2
            } else if (higher == 7 || higher == 11) {
                note = scale.get(7)
            } else if (higher == 5 || higher == 10) {
                note = scale.get(10) // es MI

                //freq_asociada = validos[0]/3;
            }
        } else if (less == 3) {
            if (higher == 7 || higher == 10) {
                note = scale.get(3) // es LA
            } else if (higher == 6 || higher == 11) {
                note = scale.get(11) // es FA
            } else if (higher == 8) {
                note = scale.get(8) // es RE

                //freq_asociada = validos[1]/3;
            }
        } else if (less == 4) {
            if (higher == 8 || higher == 11) {
                note = scale.get(4) // es 4
            } else if (higher == 7) {
                note = scale.get(0)
            } else if (higher == 9) {
                note = scale.get(9) // es MIb

                //freq_asociada = validos[1]/3;
            }
        } else if (less == 5) {
            if (higher == 8) {
                note = scale.get(1)
            } else if (higher == 9) {
                note = scale.get(5) // es 5
            } else if (higher == 10) {
                note = scale.get(10) // es MI

                //freq_asociada = validos[1]/3;
            }
        } else if (less == 6) {
            if (higher == 9) {
                note = scale.get(2)
            } else if (higher == 10) {
                note = scale.get(6) // es 6
            } else if (higher == 11) {
                note = scale.get(11)
            }
        } else if (less == 7) {
            if (higher == 10) {
                note = scale.get(3)
            } else if (higher == 11) {
                note = scale.get(7) // es 7
            }
        } else if (less == 8) {
            if (higher == 11) {
                note = scale.get(4)
            }
        } else {
            note = "FALLO"
        }
        return note
    }

    private fun returnPitch(data: DoubleArray): Double {
        // indice o poscion en el que empezaremos a leer en la trama del espectro
        // que se corresponde con la frecuencia mínima del rango de deteccion


        // indice o poscion en el que empezaremos a leer en la trama del espectro
        // que se corresponde con la frecuencia mínima del rango de deteccion
        val min_frequency_fft =
            (MIN_FREQUENCY * view.blockSize_buffer / frequency).roundToInt()
        // indice o poscion en el que acabaremos de leer en la trama del espectro
        // que se corresponde con la frecuencia máxima del rango de deteccion
        // indice o poscion en el que acabaremos de leer en la trama del espectro
        // que se corresponde con la frecuencia máxima del rango de deteccion
        val max_frequency_fft = (MAX_FREQUENCY * view.blockSize_buffer / frequency).roundToInt()
        var best_frequency =
            min_frequency_fft.toDouble() // inicializamos la frecuencia candidata en el minimo

        var best_amplitude =
            0.0 // inicializamos a 0 la amplitud que al final sera la maxima de la trama

        // recorremos la trama
        for (i in min_frequency_fft..max_frequency_fft) {

            // calcula la frecuncia actual restaurando el valor del indice o posicon
            val current_frequency: Double = (i * 1.0 * frequency / view.blockSize_buffer)


            //final double normalized_amplitude = Math.abs(data[i]);

            // calcula la amplitud actual
            val current_amplitude = (Math.pow(data[i * 2], 2.0)
                    + Math.pow(data[i * 2 + 1], 2.0))

            // normaliza la amplitud actual
            val normalized_amplitude = (current_amplitude * Math.pow(
                MIN_FREQUENCY * MAX_FREQUENCY,
                0.5
            ) / current_frequency)

            // si es mayor que la anterior es candidata a ser la maxima
            if (normalized_amplitude > best_amplitude) {
                best_frequency = current_frequency
                best_amplitude = normalized_amplitude
            }
        }
        return best_frequency

    }

    private fun returnPosition(notes: String): Int {
        var position = 0
        var meets = false

        while (!meets && position < scale.size) {
            if (notes === scale[position]) {
                meets = true
            } else {
                position++
            }
        }

        return position
    }

    private fun returnHarmonics(data: DoubleArray): DoubleArray {
        var r = 0 // indice para el recorrido del vector con los armonicos

        val thresold = view.THRESHOLD.toInt() // condicion necesaria para considerarse armonico

        val frameLength: Int =
            view.FRAMELENGTH // longitud de la trama para el estudio de los armonicos


        // indice o poscion en el que empezaremos a leer en la trama del espectro
        // que se corresponde con la frecuencia mínima del rango de deteccion


        // indice o poscion en el que empezaremos a leer en la trama del espectro
        // que se corresponde con la frecuencia mínima del rango de deteccion
        val min_frequency_fft = (MIN_FREQUENCY
                * view.blockSize_buffer / frequency).roundToInt().toInt()

        // indice o poscion en el que acabaremos de leer en la trama del espectro
        // que se corresponde con la frecuencia máxima del rango de deteccion

        // indice o poscion en el que acabaremos de leer en la trama del espectro
        // que se corresponde con la frecuencia máxima del rango de deteccion
        val max_frequency_fft = (MAX_FREQUENCY
                * view.blockSize_buffer / frequency).roundToInt().toInt()
        var best_frequency =
            min_frequency_fft.toDouble() // inicializamos la frecuencia candidata en el minimo

        var best_amplitude = 0.0 // inicializamos la amplitud a comparar con el umbral a 0

        val aux2: DoubleArray // declaracion de vector auxiliar para el estudio de la trama

        aux2 = DoubleArray(frameLength) // sera el array que contenga los armonicos

        for (i in min_frequency_fft until max_frequency_fft step frameLength){



            for (i in 0 until frameLength) {
                val current_frequency: Double =
                    ((i + j) * 1.0 * frequency / view.blockSize_buffer)

                //final double normalized_amplitude = Math.abs(data[i+j]);

                //final double normalized_amplitude = Math.abs(data[i+j]);
                val current_amplitude = (Math.pow(data[((i + j) * 2).toInt()], 2.0)
                        + Math.pow(data[((i + j) * 2 + 1).toInt()], 2.0))
                val normalized_amplitude = (current_amplitude
                        * Math.pow(MIN_FREQUENCY * MAX_FREQUENCY, 0.5) / current_frequency)

                if (normalized_amplitude > best_amplitude) {
                    best_frequency = current_frequency
                    best_amplitude = normalized_amplitude
                }
            }
            if (best_amplitude > thresold) {
                // almacena en aux2 la posicion frecuencia
                // que cumple el requisito 'umbral'
                // y en aux3 la amplitud correspondiente
                aux3[r] = best_amplitude
                aux2[r] = best_frequency
                r += 1
            }
        }


        return aux2

    }


    private fun drawSpectrum(plotSpectrum: DoubleArray) {
// Claculo del la relacion Señal a Ruido (dB)
        // Resulta del cociente entre el valor maximo del espectro entre el pormedio
        // Lo ideal es que la SNR valga infinio, lo que significa que no hay ruido
        //double snr2 = 10*Math.log10(max(trama_espectro,0,trama_espectro.length).valor/promedio(trama_espectro));


        // Claculo del la relacion Señal a Ruido (dB)
        // Resulta del cociente entre el valor maximo del espectro entre el pormedio
        // Lo ideal es que la SNR valga infinio, lo que significa que no hay ruido
        //double snr2 = 10*Math.log10(max(trama_espectro,0,trama_espectro.length).valor/promedio(trama_espectro));
        view.canvas.drawColor(Color.BLACK)

        for (i in plotSpectrum.indices) {
            val x: Int = i
            val upy: Float = view.chartHeight
            view.canvas.drawLine(
                x.toFloat(),
                (view.chartHeight - plotSpectrum.get(i) * view.chartHeight).toFloat(),
                x.toFloat(),
                upy.toFloat(),
                view.paint
            )
        }

        //paint3.setAntiAlias(true);
        //paint3.setFilterBitmap(true);
        //paint3.setTextSize(TAM_TEXT2);
        //canvas3.drawText(" SNR: " + df1.format(snr2) + " dB", blockSize_grafica-alTuraGrafica, TAM_TEXT3, paint3);


        //paint3.setAntiAlias(true);
        //paint3.setFilterBitmap(true);
        //paint3.setTextSize(TAM_TEXT2);
        //canvas3.drawText(" SNR: " + df1.format(snr2) + " dB", blockSize_grafica-alTuraGrafica, TAM_TEXT3, paint3);
        view.imageView01.invalidate()
    }

    // Metodo para enventanar Hamming un vector de muestras.
    private fun applyHamming(plot: DoubleArray): DoubleArray {
        val A0 = 0.53836
        val A1 = 0.46164
        val Nbf = plot.size
        for (k in 0 until Nbf) {
            plot[k] = plot[k] * (A0 - A1 * cos(2 * Math.PI * k / (Nbf - 1)))
        }
        return plot
    }

    private fun calculateVariance(plot: DoubleArray): Double {
        val N = plot.size
        val mean = calculateMean(plot)
        var variance = 0.0
        for (k in 0 until N) {
            variance += (plot[k] - mean).pow(2.0)
        }
        variance /= (N - 1)
        return variance

    }

    private fun calculateMean(plot: DoubleArray): Double {
        val N: Int = plot.size
        var mean = 0.0
        for (k in 0 until N) {
            mean += plot[k]
        }
        mean /= N
        return mean

    }

    private fun normalize(plot: DoubleArray): DoubleArray {

        var max = 0.0

        for (i in plot.indices) {
            if (abs(plot[i]) > max) {
                max = abs(plot[i])
            }
        }
        for (i in plot.indices) {
            plot[i] = plot[i] / max
        }
        return plot
    }

    fun max(x: DoubleArray, first: Int, end: Int): Maximum {
        val mMaxsimum = Maximum()
        for (i in first until end) {
            if (abs(x[i]) >= mMaxsimum.value) {
                mMaxsimum.value = abs(x[i])
                mMaxsimum.pos = i
            }
        }
        return mMaxsimum
    }

}