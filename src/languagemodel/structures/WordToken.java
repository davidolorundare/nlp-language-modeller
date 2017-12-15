package languagemodel.structures;

/**
 * This is a storage class for holding
 * unigram and bigram words.
 * 
 * @author David Olorundare
 *
 */
public class WordToken 
{

	//============================================ PRIVATE VARIABLES =============================================================

	
	// Represents a unigram word.
	public String unigram = " ";
	
	// Represents a bigram word.
	public String bigramW = " ";
	
	// Represents the first word in a bigram.
	public String firstWord = " ";
	
	// Represents the second word in a bigram.
	public String secondWord = " ";
		
	// Represents the count of the word (firstword, secondword - count)
	public int count = 0;
	
	// Represents the probability of the word (firstword, secondword - probability)
	public float probability = 0;
	
	
	//============================================ CONSTRUCTOR =============================================================
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that splits
	 * a bigram word into its
	 * first and second words.
	 */
	public void splitWords()
	{
		firstWord = bigramW.split(" ")[0];
		secondWord = bigramW.split(" ")[1];
	}
	
	
	
	//============================================ PRIVATE METHODS =============================================================
	
	// No Private Methods
	
	
	
}
