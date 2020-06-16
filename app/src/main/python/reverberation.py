import numpy as np

import matplotlib.pyplot as plt

from scipy.io import wavfile

def rt60(wav, img):
    plt.cla()  # Clear axis
    plt.clf()  # Clear figure
    plt.close()  # Close a figure window
    sample_rate, data = wavfile.read(wav)
    # plt.subplot(2, 1, 1)
    # plt.ylabel('Freqency (hz)')
    spectrum, freqs, t, im = plt.specgram(data, Fs=sample_rate, NFFT=1024, cmap=plt.get_cmap('twilight_shifted_r'))

    # you can choose a frequency which you want to check

    # print(freqs)

    index_of_frequency = np.where(freqs == 516.796875)[0][0]

    # find a sound data for a particular frequency

    data_for_frequency = spectrum[index_of_frequency]

    # change a digital signal for a values in decibels

    data_in_db = 10 * np.log10(data_for_frequency)

    plt.figure(2)
    plt.subplot(1, 1, 1)
    plt.plot(t, data_in_db, linewidth=1, alpha=0.7, color='#004bc6')
    plt.xlabel('Time (s)')
    plt.ylabel('Power (dB)')

    # find a index of a max value

    index_of_max = np.argmax(data_in_db)

    value_of_max = data_in_db[index_of_max]

    plt.plot(t[index_of_max], data_in_db[index_of_max], 'go')

    # slice our array from a max value

    sliced_array = data_in_db[index_of_max:]

    value_of_max_less_5 = value_of_max - 5

    # find a nearest value because it is a big chance that you won't find exactly a value_of_max_less_5 value

    def find_nearest_value(array, value):
        array = np.asarray(array)

        idx = (np.abs(array - value)).argmin()

        return array[idx]

    value_of_max_less_5 = find_nearest_value(sliced_array, value_of_max_less_5)

    index_of_max_less_5 = np.where(data_in_db == value_of_max_less_5)

    plt.plot(t[index_of_max_less_5], data_in_db[index_of_max_less_5], 'yo')

    # slice our array from a max-5dB

    value_of_max_less_25 = value_of_max - 25

    value_of_max_less_25 = find_nearest_value(sliced_array, value_of_max_less_25)

    index_of_max_less_25 = np.where(data_in_db == value_of_max_less_25)

    plt.plot(t[index_of_max_less_25], data_in_db[index_of_max_less_25], 'ro')
    # plt.show()
    # plt.savefig('graph.png')

    rt20 = (t[index_of_max_less_5] - t[index_of_max_less_25])[0]

    rt60 = 3 * rt20

    # print(round(abs(rt60), 2))

    # linear regression

    from scipy import stats

    # find a value which is 35dB less than our max

    value_of_max_less_35 = value_of_max - 35

    value_of_max_less_35 = find_nearest_value(sliced_array, value_of_max_less_35)

    index_of_max_less_35 = np.where(data_in_db == value_of_max_less_35)[0][0]

    # slice arrays to from max to max-35dB to calculate a linear regression for it

    x = t[index_of_max:index_of_max_less_35]

    y = data_in_db[index_of_max:index_of_max_less_35]

    # you do not have to worry if the gap between min value in y array and max value is a bit more than 35 dB

    slope, intercept, r_value, p_value, std_err = stats.linregress(x, y)

    plt.plot(x, intercept + slope * x, 'r', label='linear regression')

    linregress = intercept + slope * x

    linregress_data = intercept + slope * x

    index_of_max = 0

    value_of_max_less_20 = linregress_data[0] - 20

    value_of_max_less_20 = find_nearest_value(linregress_data, value_of_max_less_20)

    index_of_max_less_20 = np.where(linregress_data == value_of_max_less_20)[0][0]

    rt20 = (x[index_of_max] - x[index_of_max_less_20])

    rt60 = 3 * rt20

    # print(round(abs(rt60), 2))  # result = 0.98s
    plt.savefig(img)

    return round(abs(rt60), 2)