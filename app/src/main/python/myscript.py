import acoustics.room as room
import acoustics.bands as band
import matplotlib.pyplot as plt
import numpy as np
from scipy.io import wavfile

def rt60(wav, img):

    plt.cla()   # Clear axis
    plt.clf()   # Clear figure
    plt.close() # Close a figure window
    filePath = wav
    octave_bands = band.octave(100,8000)
    sample_rate, data = wavfile.read(filePath)
    plt.ylabel('Freqency (hz)')
    plt.xlabel('Time(sec)')

    spectrum, freqs, t, im = plt.specgram(data, Fs=sample_rate, NFFT=1024, cmap=plt.get_cmap('twilight_shifted_r'))
    t60 = room.t60_impulse(filePath, octave_bands, 't30')

    plt.savefig(img)
    np.set_printoptions(formatter={'float_kind': lambda x: "{0:0.3f}".format(x)})

    return np.round(t60, 3)
