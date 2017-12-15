# Natural Language Processing: Language Modeller

Academic Project - An NLP Language Modelling program.

## Overview:

This commandline program implements an NLP unigram and bigram language modeler.  Models are created from a given training data and evaluated with given test datasets.  
Internally the Stanford CoreNLP library is used in performing word-tokenization, sentence segmentation, and other core nlp-tasks. Aside from the performance metrics outputted with the final evaluation results, other operations can be performed during computation, including (if specified by the user):

- Computation of the Perplexity of the whole test dataset.
- Random Generation of new sentences
- Laplace smoothing operations on the training dataset.


## Screenshots:


### Program execution: the tiny-train.txt training data and tiny-test.txt testing data files on the terminal

![alt text](https://github.com/davidolorundare/image-repo/blob/master/language-modeler-images/tiny-execution.png "NLP Language Modelling program running with training and testing data files on the terminal")



### Program execution: the HG-train.txt training data and HG-test.txt testing data files on the terminal

![alt text](https://github.com/davidolorundare/image-repo/blob/master/language-modeler-images/hg-execution1.png "NLP Language Modelling program running with training and testing data files on the terminal")

---

![alt text](https://github.com/davidolorundare/image-repo/blob/master/language-modeler-images/hg-execution2.png "NLP Language Modelling program running with training and testing data files on the terminal")

---


## Usage:

The build folder, in this repository, contains all the executable files needed for running the program. Download the folder to your desktop first.

Open a terminal (or commandline shell) and navigate to the build directory. i.e. '/build/'
The format for running the program is:

>> java -cp <path_to_corenlp_library> languagemodel.core.LanguageModelerMain <path_to_training_data_file> <path_to_testing_data_file> <path_to_output_file_to_store_results> <-S | -P | -G
number_of_sentences_to_generate>

where ‘LanguageModelerMain.java’ is the name of the program, 'path_to_corenlp_library' is the location of the Stanford CoreNLP libraries used by program to perform certain NLP-tasks, ‘path_to_training_data_file’ is the location of the text file containing the training data to be used to build the unigram and bigram language models, the ‘path_to_testing_data_file’ is the location of the text file containing the test data that will be used to evaluate the language models, the ‘path_to_output_file_to_store_result’ is the location of the text file where the final results of the operations would be stored.

The additional operations such as adding smoothing, computing perplexity, and randomly generating new sentences from the bigram language model, can be enabled by appending either one, two (or all) combinations of the following command-line switches:

- ‘-S’, 
- ‘-P’, and/or 
- ‘-G’ respectively.

(‘-S’ adds smoothing, ‘-P’ computes perplexity, ‘-G’ ‘x’ randomly generates ‘x’ number of sentences.)

If ‘-G’ is appended, to enable random sentence-generation, it needs to be followed
by a number that specifies the number of new randomly-generated sentences that should be returned in the output. 


For example, while in the 'build' directory;

This command will run the program using the data in the ‘HG-train.txt’ file to train/build the language models, with smoothing enabled, randomly-generating six new sentences, and using the test-data in the ‘HG-heldout50.txt’ file to evaluate the language models. 
Returned to the console is the sentence-probability (of each sentence in the test-data), average probability of the whole dataset, and the perplexity of the whole test-dataset. The results will also be stored in the given ‘output-HG-test.txt’ file:

```>> java –cp .:stanford-corenlp-3.7.0.jar languagemodel.core.LanguageModelerMain "data/train/HG-train.txt" "data/test/HGheldout50.txt" "data/output/output-HG-test.txt" –S –P –G 6```



This command will run the same test-data sentence-probability operations, with smoothing, as the previous, but without computing perplexity nor generating any new random sentence:

```>> java –cp .:stanford-corenlp-3.7.0.jar languagemodel.core.LanguageModelerMain "data/train/tiny-train.txt" "data/test/tiny-test.txt" "data/output/myOutput.txt" –S```



This command will run the same operations as mentioned earlier except without smoothing,
without computing perplexity nor without new random-sentence generation:

```>> java –cp .:stanford-corenlp-3.7.0.jar languagemodel.core.LanguageModelerMain "data/train/HG-train.txt" "data/test/HG-heldout50.txt" "data/output/myOutput.txt" –P```



This command will run all the same operations as mentioned earlier without smoothing and without perplexity, but randomly generates four (4) new sentences on output: 

```>> java –cp .:stanford-corenlp-3.7.0.jar languagemodel.core.LanguageModelerMain "data/train/tiny-train.txt" "data/test/tiny-test.txt" "data/output/myOutput.txt" –G 4```


 

---

## Running Demo:

![alt text](https://media.giphy.com/media/xUNd9PG4okMXFuzrtC/giphy.gif "program running in terminal")

---

