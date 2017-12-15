package languagemodel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import edu.stanford.nlp.ling.DocumentReader;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import languagemodel.compute.LanguageModeler;
import languagemodel.structures.AnalyzedData;


/**
 * This class handles file processing of the input 
 * training and test data files and further language 
 * model analysis.
 * 
 * 
 * 
 * @author David Olorundare
 *
 */
public final class FileHandler 
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Represents the filepath of a file containing the training dataset. 
	private String trainingDataSource;
	
	// Represents the filepath of a file containing the results of the text analysis.
	private String outputDestination; 
	
	// Represents the filepath of a file containing the test-dataset
	private String testingDataSource;
	
	// Represents the external file to which the training dataset is gotten from.
	File trainingData;
	
	// Represents the external file to which the testing dataset is gotten from.
	File testingData;
	
	// Represents the external file to which the results of the language model analysis are stored in.
	File outputResults;
	
	// Holds an instance to this class.
	private volatile static FileHandler instance;
	
	// Represents the text processor used in the analysis of text data from a file.
	private LanguageModeler textProcessor;
	
	// Represents the data structure used for storing all the results of the text analysis.
	private AnalyzedData processedResults = new AnalyzedData();

	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 *  Private Constructor of the FileHandler class.
	 */
	private FileHandler(){	}
	
	
  /**
   * Returns a singleton instance of the FileHandler class,
   * ensuring that only one instance of the Handler is active 
   * at any single time.
   * 
   */
	public static FileHandler getInstance() 
	{
      if (instance == null)
      {
          synchronized (FileHandler.class)
          {
              if (instance == null)
              {
                  instance = new FileHandler();
              }
          }
      }
      return instance;
   }
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that reads in a 
	 * dataset and creates a CoreNLP annotation
	 * from it uses for further language model processing.
	 * 
	 * @param textData	the dataset to be read.
	 * 
	 * @return	a CoreNLP annotation of the given dataset.
	 * 
	 * @throws FileNotFoundException if the file containing the data does not exist.
	 * 
	 * @throws IOException	if an error occurs while reading the data.
	 * 
	 */
	public Annotation readData(String textData) throws FileNotFoundException, IOException
	{
        String text =   " ";        
        InputStream input = new FileInputStream(textData);
        Reader dataReader = new InputStreamReader(input, "WINDOWS-1252");
		
		text = DocumentReader.readText(dataReader);
		
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
		
        input.close();
		return document;	
	}
	
	/**
	 * Helper method that sets the current filepath
	 * of the input training dataset.
	 * 
	 * @param filePath	current filepath of the training dataset.
	 * 
	 */
	public void setTrainDataFilePath(String filePath)
	{
		trainingDataSource = filePath;
	}
	
	
	/**
	 * Helper method that sets the current filepath
	 * of the test-dataset used for evaluating the 
	 * language model.
	 * 
	 * @param filePath	current filepath of the test-dataset.
	 * 
	 */
	public void setTestDataFilePath(String filePath)
	{
		testingDataSource = filePath;
	}
	
	
	/** 
	 * Helper method that sets the current filepath
	 * of the output text-file used for storing the
	 * results of the langugage model operations.
	 * 
	 * @param filePath	current filepath of the output text-file.
	 * 
	 */
	public void setOutputResultFilePath(String filePath)
	{
		outputDestination = filePath;
	}
	
	
	/**
	 * Loads data from a file containing text and
	 * delegates its language model creation and operation
	 * implementation. 
	 * 
	 * @param	analyzer	the language model implementation used to 
	 * 						analyze the given dataset from a file.
	 * 
	 * @param	ops			additional operations that should be performed on the language model.
	 * 
	 * @return	a structure containing the results of the 
	 * 			language model operations.
	 *
	 * @throws IOException	if an error occurs while reading the input file.
	 * @throws FileNotFoundException	if the input or output text files are empty or cannot be found.
	 * 
	 */
	public AnalyzedData loadAndCompute(LanguageModeler analyzer, HashMap<String, Integer> ops) throws IOException, FileNotFoundException
	{
		textProcessor = analyzer;
		
        // Pass the datasets into CoreNLP to begin language model operations.
        processedResults = textProcessor.analyzeText(trainingDataSource, testingDataSource, ops);
                 
        // Return the text analysis for printing/storage into an external file. 
        return processedResults;
	}
	
	
	/**
	 * Helper method that writes some string data
	 * to the given external output file.
	 * 
	 * @param data	the string data to be written to 
	 * 				a given external file.
	 * 
	 * @throws IOException if an error occurs while reading the input file.
	 */
	public void writeToFile(String data) throws IOException
	{
		Writer textFileWriter = new FileWriter(outputDestination);
		
		textFileWriter.write(data);
		textFileWriter.close();
	}
	
		
	/**
	 * Helper method that pre-processes a given
	 * sentence by appending the <s> start and end </s> 
	 * sentence symbols to the sentence.
	 * 
	 * @param sentence	the sentence that is to be appended with <s> and </s>
	 * 
	 * @return	a pre-processed sentence.
	 */
	public String appendToSentence(CoreMap sentence)
	{
		String prefix = "<s> ";
    	String suffix = " </s>";
		
		// Preprocess by appending the <s> and </s> symbols to each sentence.
    	String prefixSentence = prefix.concat(sentence.toString());
    	if (prefixSentence.endsWith(".")) { prefixSentence = prefixSentence.substring(0,prefixSentence.length() - 1) + " ."; }		
    	String AppendedSentence = prefixSentence.concat(suffix);
		
    	return AppendedSentence;
	}
	
	
	/**
	 * Helper method that pre-processes a given
	 * sentence by appending the <s> start symbol
	 * to the sentence.
	 * 
	 * @param sentence	the sentence that is to be appended with <s>
	 * 
	 * @return	a pre-processed sentence.
	 */
	public String appendPrefixToSentence(CoreMap sentence)
	{
		String prefix = "<s> ";	
		// Preprocess by appending the <s> start symbol to each sentence.
    	String prefixSentence = prefix.concat(sentence.toString());
    	if (prefixSentence.endsWith(".")) { prefixSentence = prefixSentence.substring(0,prefixSentence.length() - 1) + " ."; }
    	return prefixSentence;
	}

	
	//============================================ PRIVATE METHODS =============================================================
	
	// No Private Methods

}
