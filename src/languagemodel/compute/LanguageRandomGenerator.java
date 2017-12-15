package languagemodel.compute;

import java.util.ArrayList;
import java.util.Random;


/**
 * Class represents a random sentence
 * -generator that uses the bigrams in 
 * a given training dataset to form
 * new sentences.
 * 
 * @author David Olorundare
 *
 */
public final class LanguageRandomGenerator 
{

	//============================================ PRIVATE VARIABLES =============================================================
	
	
	// Holds an instance to this class.
	private volatile static LanguageRandomGenerator instance;
	
	// Holds a list of all bigrams starting with the <s> tag.
	private ArrayList<String> startSymbolBigrams;
	
	// Holds a reference to a list of bigrams from the training dataset.
	private ArrayList<String> bigrams = new ArrayList<String>();
	
	// Holds a reference to the next bigram following a specific unigram.
	private ArrayList<String> nextBigrams;
	
	// Represents a random generator used in generating sentences out of bigrams.
	private Random randomValue = new Random();
	
	//	Holds a reference to each new sentence randomly generated.
	private StringBuilder sentence;
	
	// Holds a reference to all sentences generated in a given session.
	private ArrayList<String> generatedSentences = new ArrayList<String>();
	
	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 *  Private Constructor of the LanguageRandomGenerator class.
	 */
	private LanguageRandomGenerator(){	}
	
	
  /**
   * Returns a singleton instance of the LanguageRandomGenerator class,
   * ensuring that only one instance is active 
   * at any single time.
   * 
   */
	public static LanguageRandomGenerator getInstance() 
	{
      if (instance == null)
      {
          synchronized (LanguageRandomGenerator.class)
          {
              if (instance == null)
              {
                  instance = new LanguageRandomGenerator();
              }
          }
      }
      return instance;
   }
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that generates
	 * randomly new sentences formed from
	 * bigrams using the given training dataset
	 * a specified number of sentences to generate.
	 * 
	 * @param allBigrams	bigrams training dataset.
	 * 
	 * @param sentencesToGenerate	the number of sentences to generate.
	 * 
	 * @return	a list containing new randomly generated sentences.
	 * 
	 */
	public ArrayList<String> randomGeneration(ArrayList<String> allBigrams, int sentencesToGenerate)
	{
		startSymbolBigrams = new ArrayList<String>();
		// Store all the bigrams of the dataset.
		bigrams = allBigrams;

		// Pull out all the start symbol bigrams into their own list.
		for (String words : bigrams) { if (words.split(" ")[0].equals("<s>")){ startSymbolBigrams.add(words.trim()); } }
		nextBigrams = new ArrayList<String>();
		
		// Randomly generate new sentences
		generateSentence(sentencesToGenerate);
		
		return generatedSentences;
	}


	/**
	 * Helper method that generates a new sentence
	 * randomly using bigrams from a given training
	 * dataset.
	 * 
	 * @param sentencesToGenerate	the number of sentences to generate.
	 */
	private void generateSentence(int sentencesToGenerate) 
	{
		String startWord;
		// Randomly generate new sentences built out of bigrams in the training dataset.
		for (int i = 0; i < sentencesToGenerate; i++)
		{
			startWord = startSymbolBigrams.get(randomValue.nextInt(startSymbolBigrams.size()));
			sentence = new StringBuilder();
			sentence.append(startWord.trim() + " ");
			
			while(true)
			{
				nextBigrams = searchForWord(bigrams, startWord.split(" ")[1]);
				startWord = nextBigrams.get(randomValue.nextInt(nextBigrams.size()));				
				sentence.append(startWord.split(" ")[1].trim() + " ");
				
				// if end symbol </s> is encountered then stop generating sentence.
				if (startWord.split(" ")[0].equals(".") || startWord.split(" ")[1].equals(".")){ break; }
			}
			
			// Strip <s> and </s> tags before adding the sentence to the generated-sentences-list.
			 generatedSentences.add(stripSentenceTags(sentence.toString()));
		}
	}
	
	
	
	
	
	
	
	//============================================ PRIVATE METHODS =============================================================
	
	
	/**
	 * Helper method that searches for a given unigram
	 * in a list of bigrams.
	 * 
	 * @param bigrams	the list of bigrams to search through.
	 * 
	 * @param gram	the unigram to search for in the list of bigrams
	 * 
	 * @return	a list of bigrams whose first word is the unigram being searched for.
	 * 
	 */
	private ArrayList<String> searchForWord(ArrayList<String> bigrams, String gram)
	{
		ArrayList<String> foundWords = new ArrayList<String>();
		for (String word : bigrams)
		{
			if (word.split(" ")[0].equals(gram) ){ foundWords.add(word.trim()); }
		}
		return foundWords;
	}
	
	
	
	/**
	 * Helper method that strips the 
	 * <s> sentence beginning tag and </s>
	 * end tag from a generated sentence.
	 * 
	 * @param wrangledSentence	the generated sentence to strip.
	 * 
	 * @return	a stripped-out sentence without <s> tags.
	 * 
	 */
	private String stripSentenceTags(String wrangledSentence)
	{
		if(wrangledSentence.startsWith("<s>"))
		{ wrangledSentence = wrangledSentence.substring(3,wrangledSentence.length() - 1).trim(); }
		
		if ( wrangledSentence.endsWith("</s>")) 
		{ wrangledSentence = wrangledSentence.substring(0,wrangledSentence.length()-1); wrangledSentence.concat("."); }
		
		return wrangledSentence;
	}
	
	
}
