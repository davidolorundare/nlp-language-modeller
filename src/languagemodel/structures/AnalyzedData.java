package languagemodel.structures;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class represents the statistics
 * on the test dataset analyzed from the 
 * evaluation of the language models.
 * 
 * @author David Olorundare
 *
 */
public class AnalyzedData
{
	
	//============================================ PRIVATE VARIABLES =============================================================
	
	
	// Represents all the sentences in the dataset and their respective unigram and bigram probabilities.
	private HashMap<String, float[]> documentSentenceInfo;
	
	// Represents the average unigram probability of the dataset.
	private float AverageUnigramProbability;
	
	// Represents the average bigram probability of the dataset.
	private float AverageBigramProbability;
	
	// Contains the unigram and bigram perplexities of the dataset.
	private float[] documentPerplexity;
	
	// Represents a list of random new sentences generated using the training dataset.
	ArrayList<String> randomGeneratedSentences;
	
	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 * Constructor of the class.
	 * 
	 */
	public AnalyzedData(){	}
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that stores all the computed results
	 * of the language model operations, for outputting
	 * to the console and an external file.
	 * 
	 * @param document	contains each sentence in the dataset and their respective unigram and bigram probabilities.
	 * 
	 * @param unigramProbabilityAvg	the average unigram probability of the dataset.
	 * 
	 * @param bigramProbabilityAvg	the average bigram probability of the dataset.
	 * 
	 * @param datasetPerplexity	contains the unigram and bigram perplexities of the dataset.
	 * 
	 * @param randomSentences	a list of random new sentences generated using the training dataset.
	 * 
	 */
	public void addResults(HashMap<String, float[]> document, float unigramProbabilityAvg, float bigramProbabilityAvg, float[] datasetPerplexity, ArrayList<String> randomSentences  )
	{
		setDocumentSentenceInfo(document);
		setAverageUnigramProbability(unigramProbabilityAvg);
		setAverageBigramProbability(bigramProbabilityAvg);
		setDocumentPerplexity(datasetPerplexity);
		setGeneratedSentences(randomSentences);
	}
	
	
	/**
	 * Helper method that sets the sentences in the dataset with their respective unigram and bigram probabilities.
	 * 
	 * @param value structure containing the sentences in the dataset with their respective unigram and bigram probabilities.
	 */
	public void setDocumentSentenceInfo(HashMap<String, float[]>  value) 
	{ documentSentenceInfo = value; }
	
	
	/**
	 * Helper method that sets the average unigram probability of the dataset.
	 * 
	 * @param value the average unigram probability of the dataset.
	 */
	public void setAverageUnigramProbability(float value) 
	{ AverageUnigramProbability = value; }

	
	/**
	 * Helper method that sets the average bigram probability of the dataset.
	 * 
	 * @param value the average bigram probability of the dataset.
	 */
	public void setAverageBigramProbability(float value) 
	{ AverageBigramProbability = value; }
	
	
	/**
	 * Helper method that sets the structure containing the unigram and bigram perplexities of the dataset.
	 * 
	 * @param value structure containing the unigram and bigram perplexities of the dataset.
	 */
	public void setDocumentPerplexity(float[] value) 
	{ documentPerplexity = value; }
	

	/**
	 * Helper method that sets a list of random new sentences generated using the training dataset.
	 * 
	 * @param value	list of random new sentences generated using the training dataset.
	 */
	public void setGeneratedSentences(ArrayList<String> value) 
	{ randomGeneratedSentences = value; }
		
	
	/**
	 * Helper method that returns the sentences in the dataset with their respective unigram and bigram probabilities.
	 * 
	 * @return sentences in the dataset with their respective unigram and bigram probabilities
	 */
	public HashMap<String, float[]> getDocumentSentenceInfo() 
	{ return documentSentenceInfo; }


	/**
	 * Helper method that returns the average unigram probability of the dataset.
	 * 
	 * @return the average unigram probability of the dataset.
	 */
	public float getAverageUnigramProbability() 
	{ return AverageUnigramProbability; }


	/**
	 * Helper method that returns the average bigram probability of the dataset.
	 * 
	 * @return the average bigram probability of the dataset.
	 */
	public float getAverageBigramProbability() 
	{ return AverageBigramProbability; }


	/**
	 * Helper method that returns the structure containing the unigram and bigram perplexities of the dataset.
	 * 
	 * @return structure containing the unigram and bigram perplexities of the dataset.
	 */
	public float[] getDocumentPerplexity() 
	{ return documentPerplexity; }
	
	
	/**
	 * Helper method that returns a list of random new sentences generated using the training dataset.
	 * 
	 * @return list of random new sentences generated using the training dataset.
	 */
	public ArrayList<String> getRandomGeneratedSentences()
	{ return randomGeneratedSentences; }

		
	//============================================ PRIVATE METHODS =============================================================
	
	// No Private Methods.
	
}
