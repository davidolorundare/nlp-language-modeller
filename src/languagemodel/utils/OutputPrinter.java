package languagemodel.utils;

import java.io.IOException;
import languagemodel.structures.AnalyzedData;


/**
 * Printing Utility class that outputs information
 * to the console about the dataset language model 
 * analysis and also stores it in a given output-file.
 * 
 * @author David Olorundare
 *
 */
public final class OutputPrinter 
{
	
	//============================================ PRIVATE VARIABLES =============================================================
	
	
	// Holds an instance to this class
	private volatile static OutputPrinter instance;
	
	// Represents the number of sentences in the dataset.
	private int sentenceCount = 1;
	
	// Represents the output analysis information 
	// to be displayed and stored in an external file.
	StringBuilder output;
	
	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 * Private Constructor of the OutputPrinter class.
	 * 
	 */
	private OutputPrinter(){	}
	
	
	/**
	  * Returns a singleton instance of the OutputPrinter class,
	  * ensuring that only one instance of the class is active 
	  * at any single time.
	  * 
	  */
	public static OutputPrinter getInstance() 
	{
		if (instance == null)
	      {
	          synchronized (OutputPrinter.class)
	          {
	              if (instance == null)
	              {
	                  instance = new OutputPrinter();
	              }
	          }
	      }
	      return instance;
	}
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that prints data analysis
	 * of the language model evaluation information.
	 * 
	 * @param data	the results of the language model evaluation to print out.
	 * 
	 */
	public void printAnalysisToScreen(AnalyzedData data) throws IOException
	{
		output = new StringBuilder();
		output.append("\n");
		
		// Append document sentences and probabilities.
		appendDocumentSentence(data);
		
		// Append average sentence probabilities.
		appendAvgProbabilityInfo(data);
		
		// Append document perplexities.
		appendPerplexityInfo(data);
		
		// Append randomly generated sentences. 
		appendGeneratedSentences(data);
		
		// Print the analysis results to the console.
		System.out.println(output.toString());
		
		// Save the analysis results to an external file.
		printAnalysisToFile(output.toString());
	}


	//============================================ PRIVATE METHODS =============================================================
	
	
	/**
	 * Helper method that appends to the output 
	 * a list of randomly generated sentences
	 * from the dataset.
	 * 
	 * @param data the structure containing randomly generated sentences.
	 * 
	 */
	private void appendGeneratedSentences(AnalyzedData data) 
	{
		// Output randomly generated sentences.
		if (data.getRandomGeneratedSentences() != null)
		{
			output.append("\n------------\n");
			output.append("*Randomly Generated Sentences:\n\n");
			for(String sentence : data.getRandomGeneratedSentences())
			{
				output.append(sentence + "\n");
			}
		}
	}


	/**
	 * Helper method that appends to the output 
	 * the average unigram and bigram probabilities 
	 * of the test dataset.
	 * 
	 * @param data	the structure containing the average unigram and bigram probabilities of the test dataset.
	 * 
	 */
	private void appendAvgProbabilityInfo(AnalyzedData data) 
	{
		// Output average probability information.
		output.append("==========================\n");
		output.append("* Probability:\n");
		output.append("- Average unigram probability: " + data.getAverageUnigramProbability() + "\n");
		output.append("- Average bigram probability: " + data.getAverageBigramProbability() + "\n");
	}


	/**
	 * Helper method that appends to the output 
	 * the unigram and bigram perplexities of 
	 * test the dataset.
	 * 
	 * @param data	the structure containing the perplexities of the test dataset.
	 * 
	 */
	private void appendPerplexityInfo(AnalyzedData data) 
	{
		// Output perplexity information.
		if (data.getDocumentPerplexity() != null)
		{
			output.append("\n----------\n");
			output.append("*Perplexity:\n");
			output.append("- Unigram perplexiy: " + data.getDocumentPerplexity()[0] + "\n");
			output.append("- Bigram perplexiy:  " + data.getDocumentPerplexity()[1] + "\n");
		}
	}


	/**
	 * Helper method that appends to the output 
	 * sentences in a dataset and their unigram
	 * and bigram probabilities.
	 * 
	 * @param data	the structure containing sentences, unigram and bigram probabilities of the test dataset.
	 * 
	 */
	private void appendDocumentSentence(AnalyzedData data) 
	{
		// Output sentence and probability information.
		for(String sentence : data.getDocumentSentenceInfo().keySet())
		{
			output.append("Sentence " + sentenceCount + ": " + sentence + "\n");
			output.append("- unigram [Prob] " + data.getDocumentSentenceInfo().get(sentence)[0] + "\n");
			output.append("- bigram  [Prob] " + data.getDocumentSentenceInfo().get(sentence)[1] + "\n\n");
			sentenceCount++;
		}
	}
		
	
	/**
	 * Helper method that prints some given
	 * language model evaluation analysis data to an external file.
	 * 
	 * @param data	the language model evaluation analysis information to store in an external file.
	 * 
	 * @throws IOException	if an error occurs while reading the external file.
	 */
	private void printAnalysisToFile(String data) throws IOException
	{
		FileHandler saveToFile = FileHandler.getInstance();
		
		saveToFile.writeToFile(data);
		
	}

	
}
