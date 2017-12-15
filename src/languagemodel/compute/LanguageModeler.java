package languagemodel.compute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.StringUtils;
import languagemodel.structures.AnalyzedData;
import languagemodel.structures.WordToken;
import languagemodel.utils.FileHandler;


/**
 * This class performs various language model
 * operations on a given training and testing
 * dataset.
 * The class has methods for creating models,
 * Calculating the log probability and perplexity
 * of a sentence (with or without smoothing), as 
 * well as random-generation of new sentences. 
 * 
 * @author David Olorundare
 *
 */
public final class LanguageModeler 
{
	
	//============================================ 	PRIVATE VARIABLES =============================================================
	
	
		//==================== INITIALIZATION VARIABLES ============================
	
	// Represents an instance to this class.
	private volatile static LanguageModeler instance;

	// Represents a the filepath of the training dataset.
	private String trainingData;

	// Represents a the filepath of the testing dataset.
	private String testingData;
	
	// Represents the CoreNLP processor used for language model computation.
	private StanfordCoreNLP dataPipeline;
	
		
 		//========================== TOKEN AND SENTENCE VARIABLES ==================
 	
 	// Represents the number of tokens in the training dataset.
 	private int tokenCount = 0;
 	
 	// Represents the number of sentences in the training dataset.
 	private int sentenceCount = 0; 
 	
 	// Represents the set of all sentences in the training dataset.
  	private String[] lineSentences;
 	
 	// Represents the count of all the word-tokens in the test dataset.
 	private int testDataTokens = 0;
 	
 	// Represents the count of all the sentences in the test dataset.
 	private int testDataSentences = 0;
 	
 	// Represents all word-tokens in the training dataset, inclusive of the <s> start symbol.
 	private HashMap<String, WordToken> allWordTokens = new HashMap<String, WordToken>();
 	
 	// Represents all bigrams in a given sentence.
 	private String[] allBigrams;
 	
 		//=================== PROBABILITY VARIABLES =======================
 	
 	// Represents a mapping of all sentences in a given dataset to their uni- and bigram log probabilities.
  	private HashMap<String, float[]> documentSentences = new HashMap<String, float[]>();
  	
  	// Represent the average unigram sentence probability of the given dataset.
  	private float avgUnigramSentenceProbability = 0;
  	
  	// Represent the average bigram sentence probability of the given dataset.
  	private float avgBigramSentenceProbability = 0;
  	
  	// Represents a variable used in computing the unigram perplexity.
  	private float totalUnigramPerplexityProbability = 0;
  	
  	// Represents a variable used in computing the bigram perplexity.
	private float totalBigramPerplexityProbability = 0;
	
	// Represents the unigram (first array index) and bigram (second array index) test dataset perplexity.
	private float[] testDataPerplexity;
  	
 		//================== LANGUAGE MODEL VARIABLES =======================
 	
 	// Represents the mapping of all bigrams in the training dataset to their occurrence-rate.
 	private HashMap<String, WordToken> bigramCount = new HashMap<String, WordToken>();
	
 		//======================== OPERATOR VARIABLES ======================
 	
 	// Represents a variable that determines if smoothing is enabled on bigram model.
 	private Boolean smoothing = false;
 	
 	// Represents a variable that determines if the perplexity of the testing dataset should be computed.
 	private Boolean perplexity = false;
 	
 	// Represents a variable that determines if random-sentences from the training dataset should be generated.
 	private Boolean randomSentenceGenerator = false;
 	
 	// Represents the number of new random sentences that should be generated from the training dataset.
 	private int generateSentences = 0;
 	
 	// Represents a temporary storage of the new sentences that have been randomly generated.
 	private ArrayList<String> randomGenSentences;
 	
 	// Represents the default value used to initially fill the bigram count model/table.
  	private int modelDefaultFill = 0;
 	
 		//========================== OUTPUT VARIABLES =======================
 	
 	// Represents results from the language model operations performed in this session.
 	AnalyzedData resultStats = new AnalyzedData();
 	
 	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 * Private Constructor of the LanguageModeler class.
	 * 
	 */
	private LanguageModeler() {	}
	
	
	/**
	  * Returns a singleton instance of the LanguageModeler class,
	  * ensuring that only one instance is active 
	  * at any single time.
	  * 
	  */
	public static LanguageModeler getInstance() 
	{
	      if (instance == null)
	      {
	          synchronized (LanguageModeler.class)
	          {
	              if (instance == null)
	              {
	                  instance = new LanguageModeler();
	              }
	          }
	      }
	      return instance;
	}
	

	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that returns the average
	 * unigram probability of a dataset
	 * 
	 * @return	average unigram sentence probability in a dataset.
	 * 
	 */
	public float getAverageUnigramProbability(){ return avgUnigramSentenceProbability; }
	
	
	/**
	 * Helper method that returns the average
	 * bigram probability of a dataset
	 * 
	 * @return	average unigram sentence probability in a dataset.
	 * 
	 */
	public float getAverageBigramProbability(){ return avgBigramSentenceProbability; }
	
	
	/**
	 * Performs creation of language models from
	 * a given training dataset, operates on the models,
	 * evaluates the models using a given testing dataset,
	 * and returns all the results for outputting.
	 * 
	 * @param	 trainingText	the dataset used to build the language models.
	 * 
	 * @param	 testingText	the dataset used to evaluate the built language models.
	 * 
	 * @param	 operators		additional operations that should be performed on the training dataset.
	 * 
	 * @return	structure containing the results of language model operations.
	 * 
	 * @throws	IOException	if an error occurs while reading either training or testing dataset
	 * @throws	FileNotFoundException  if either training or testing set data does not exist.
	 * 
	 */
	public AnalyzedData analyzeText(String trainingText, String testingText, HashMap<String, Integer> operators) throws FileNotFoundException, IOException
	{
		
		//=========================  OPERATE ON THE TRAINING AND TEST DATASETS  =======================================================
		
		trainingData = trainingText;
		testingData = testingText;

		// Suppress the red information lines that CoreNLP usually displays on startup.
		PrintStream nlpErrorHandler = System.err;
		System.setErr(new PrintStream(new OutputStream() { public void write(int temp) { } }));
		
		// Setup the CoreNLP pipeline for both training and testing datasets.
        dataPipeline = new StanfordCoreNLP(PropertiesUtils.asProperties("annotators", "tokenize, ssplit","tokenize.language", "en"));
        System.setErr(nlpErrorHandler);
        
        // Create an empty Annotation just with the given text
        Annotation trainDocument = FileHandler.getInstance().readData(trainingText);
        Annotation testDocument = FileHandler.getInstance().readData(testingText);
        
        // Run all Annotators for the training and test datasets.
        Long startTime = System.currentTimeMillis();
        dataPipeline.annotate(trainDocument);
        dataPipeline.annotate(testDocument);
        
        // Start performing each of the operation as required by the Homework.
		coreOperation(trainDocument, operators);
		System.out.println("Program Running\n");
		
		// Build the unigram and bigram language models, compute their probabilities.
		logProbabilityOperation(allWordTokens, bigramCount);
	
		// Perform random sentence generation if enabled.
		ArrayList<String> tokens = new ArrayList<String>();
		tokens = splitTokens(allBigrams);
		if (randomSentenceGenerator)
		{ randomGenSentences = LanguageRandomGenerator.getInstance().randomGeneration(tokens, generateSentences); }
		
		// Evaluate the language model using the test dataset.
		SentenceProbabilityOperation(testDocument);
		if (perplexity) { testDataPerplexity = computeTestDataPerplexityOperation(testDocument); }
		
		// Return results of language model operations.
		resultStats.addResults( documentSentences, getAverageUnigramProbability(), getAverageBigramProbability(), testDataPerplexity, randomGenSentences);
		
		Long stopTime = System.currentTimeMillis();
		System.out.println("Execution Time: " + (stopTime - startTime) + " ms\n");
		return resultStats;
	}
	
	
	//=================================  PRIVATE METHODS ==========================================================================================
	
	
		//========================	ANALYZE THE TRAINING AND TEST DATASETS =============================
	
	
	/**
	 * Helper method that performs the basic
	 * operations needed to construct a unigram/
	 * bigram language model from the given 
	 * training dataset, such as counting of
	 * sentences, tokens, types, and uni/bigrams.
	 * 
	 * @param doc	a given training dataset that will be used to build the uni- and bigram language models.
	 * 
	 * @param operators	a list of additional operations that should be performed on the language models.
	 * 
	 */
	private void coreOperation(Annotation doc, HashMap<String, Integer> operators)
	{        
		// Retrieve a list of all sentences from the training dataset.
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);        
		allBigrams = new String[sentences.size()];
		
		// Count each sentence.
    	sentenceCount = sentences.size();
		
		int index = 0;
        for(CoreMap sentence: sentences) 
        {
        	// Save each of the original sentences.
        	//lineSentences[index] = sentence.toString();
        	allBigrams[index] = FileHandler.getInstance().appendToSentence(sentence);
        	// Unigram counting.
        	countAllUnigrams(sentence.get(TokensAnnotation.class));   
        	// Bigram counting.
        	countBigrams(FileHandler.getInstance().appendToSentence(sentence)); 
        	index++;
        }
        
        // Create the various models that will be used: for count, log probability, and random-sentence-generation. 
        buildModels(bigramCount, operators);
	}
	

	/**
	 * Helper method that counts the number of sentences
	 * in the given test dataset.
	 * 
	 * @param doc	the given test dataset whose sentences are to be counted.
	 */
	private void testDataSentenceCounter(Annotation doc)
	{
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);      
		testDataSentences = sentences.size();
        for(CoreMap sentence: sentences) 
        { testDataTokenCounter(sentence.get(TokensAnnotation.class)); }
	}
	
	
	/**
	 * Helper method that counts the number of tokens
	 * in a given sentence from the test dataset.
	 * 
	 * @param sentence	the given test dataset sentence whose tokens are to be counted.
	 * 
	 */
	private void testDataTokenCounter(List<CoreLabel> sentence)
	{
		Collection<String> unigrams = new ArrayList<String>();
		unigrams = StringUtils.getNgramsFromTokens(sentence, 1,1);   
    	for(String token : unigrams){ testDataTokens++; }
	}
	
	
	/**
	 * Helper method that counts all unigrams
	 * in a given sentence and tracks the total
	 * number of tokens.
	 * 
	 * @param sentence	the given sentence whose unigrams are to be counted.
	 * 
	 */
	private void countAllUnigrams(List<CoreLabel> sentence)
	{
		String[] unigrams = new String[sentence.size()];
		unigrams = StringUtils.getNgramsFromTokens(sentence, 1,1).toArray(new String[0]);   
		
		tokenCount = unigrams.length;
		WordToken token;
		WordToken startSymbol;
		
    	for (String word : unigrams)
    	{
    		if (allWordTokens.get(word) == null)
    		{
    			token = new WordToken(); token.unigram = word; token.count = 1;
    			allWordTokens.put(word, token);
    		}
    		else
    		{ 
    			token = allWordTokens.get(word);
    			token.count++;
    			allWordTokens.put(word, token);	
    		}	
    	}
    	// Append the start-symbol <s> to the list of all tokens in the training set.
    	startSymbol = new WordToken();
    	startSymbol.unigram = "<s>"; startSymbol.count = sentenceCount;
    	allWordTokens.put("<s>", startSymbol);
    }
		
		
	/**
	 * Helper method that counts all the 
	 * bigrams in a given sentence.
	 * 
	 * @param sentence	the given sentence whose bigrams are to be counted.
	 * 
	 */
	private void countBigrams(String sentence)
	{
		Collection<String> bigramsList = new ArrayList<String>();
    	bigramsList = StringUtils.getNgramsString(sentence, 2, 2); 
    	String[] bigrams = new String[bigramsList.size()];
    	bigrams = bigramsList.toArray(new String[0]);

    	WordToken gram;
    	
    	for (String bigram : bigrams)
    	{
    		
    		if (bigramCount.get(bigram) == null)
    		{
    			gram = new WordToken(); gram.count = 1; gram.bigramW = bigram;
    			bigramCount.put(bigram, gram);
    		}
    		else
    		{ 
    			gram = bigramCount.get(bigram);
    			gram.count++;
    			bigramCount.put(bigram, gram);	
    		}
    	}
	}
	
	
			//=======================  BUILD UNIGRAM AND BIGRAM LANGUAGE MODELS ======================== 
	
	
	/**
	 * Helper method that builds the bigramCount model
	 * from the training dataset.
	 * 
	 * @param bigramCount	a list mapping of all bigrams in the dataset to their occurrence-rate.
	 * 
	 * @param operators		a list user-defined operations that be additionally performed such as smoothing and random-sentence generation.
	 * 
	 */
	private void buildModels(HashMap<String, WordToken> bigramCount, HashMap<String, Integer> operators )
	{
		// pull info from operator to determine if smoothing or random-sentence generation is enabled.
		for (String operation : operators.keySet() ) 
		{
			if ( operation.equals("smoothing") ) { if (operators.get(operation) == 1)  smoothing = true; }
			if ( operation.equals("perplexity") ) { if (operators.get(operation) == 1)  perplexity = true; }
			if ( operation.equals("generator") ) { generateSentences = operators.get(operation); randomSentenceGenerator = true; }
		}
		
		// Thereafter build the needed bigram-count model needed for further computation.
		createBigram2dModel(bigramCount);
	}
	

	/**
	 * Helper method that creates a two-dimensional
	 * table/model of bigrams (formed from unigrams)
	 * and their occurrence-rate which is by default 
	 * set to 0.
	 * If Smoothing is enabled, the default occurrence-rate
	 * on creation of the table/model is set to 1.
	 * 
	 * @param model	the set of bigrams in the training dataset.
	 *
	 */
	private void createBigram2dModel(HashMap<String, WordToken> model) 
	{
		// Smoothing is enabled, so prefill all counts with 1.
		if (smoothing) { modelDefaultFill = 1; }
				
		// Iterate through the model and add smoothing value.
		for (String grams : model.keySet())
		{
			model.get(grams).count += modelDefaultFill;
			// Split the bigram into two separate words.
			model.get(grams).splitWords();
		}
	}

			//=======================	COMPUTE LOG PROBABILITIES OF LANGUAGE MODELS =====================
	
	
	/**
	 * Helper method that delegates computation
	 * of both unigram and bigram log probabilities
	 * to auxiliary methods.
	 * 
	 * @param allTokens	a list mapping of all unigrams in the training dataset to their occurrence-rate.
	 * 
	 * @param bigramCountModel	a 2D model of the the bigrams in the training dataset and their occurrence-rate.
	 * 
	 */
	private void logProbabilityOperation(HashMap<String, WordToken> allTokens, HashMap<String, WordToken> bigramCountModel)
	{
		computeUnigramLogProbability(allTokens);
		computeBigramLogProbability(bigramCountModel);	
	}
	
	
	/**
	 * Helper method that computes the natural log probability
	 * of a specific Ngram given its individual 
	 * rate of occurrence and the total occurrence
	 * of Ngrams in the training dataset. 
	 * 
	 * @param event	the individual rate of occurrence of the Ngram.
	 * 
	 * @param totalOccurrence	the total occurrence of Ngrams in the training dataset
	 * 
	 * @return	the natural log of the probability of the Ngram.
	 * 
	 */
	private float computeLogProbability(int event, int totalOccurrence)
	{
		float logProbability = 0;
		float probability = ((new Integer(event).floatValue() )/(new Integer(totalOccurrence).floatValue()));
		if (probability == 0 || probability == 1) { return 0; }
		else { logProbability = (float) Math.log(probability); }
		
		return logProbability;
	}
	
	
	/**
	 * Helper method that computes the log probability of the
	 * unigrams in the training dataset.
	 * 
	 * @param tokens	the list mapping of the unigrams in the training dataset to their occurrence-rate.
	 * 
	 */
	private void computeUnigramLogProbability(HashMap<String, WordToken> tokens)
	{
		int Ncount = sumMapValues(tokens);
		
		for(String unigram : tokens.keySet())
		{
			float unigramProbability = 0;
			unigramProbability = computeLogProbability(tokens.get(unigram).count,Ncount);
			tokens.get(unigram).probability = unigramProbability;
		}
	}
	
	
	/**
	 * Helper method that computes the log probability of the
	 * bigrams in the training dataset.
	 * 
	 * @param bigramModel	the bigram-count model containing occurrences whose probabilities are to be computed.
	 * 
	 */
	private void computeBigramLogProbability(HashMap<String, WordToken> bigramModel)
	{
		float bigramProbability = 0;
		for (String bigram : bigramModel.keySet())
		{	
			for(String unigram : allWordTokens.keySet())
			{
				if (bigramModel.get(bigram).firstWord.equals(unigram))
				{
					bigramProbability = computeLogProbability( bigramModel.get(bigram).count, allWordTokens.get(unigram).count );
					bigramModel.get(bigram).probability = bigramProbability;
				}
			}
		}
	}
	
	
		//==========================	EVALUATE THE LANGUAGE MODELS =====================================
	
	
	/**
	 * Helper method that computes the unigram and bigram
	 * sentence-probabilities of all sentences in a given dataset.
	 * Also computes the average unigram and bigram sentence-
	 * probabilities.
	 * 
	 * @param doc	the dataset to compute its sentence probabilities.
	 * 
	 */
	private void SentenceProbabilityOperation(Annotation doc)
	{ 
		float unigramSentenceProbability, bigramSentenceProbability = 0;
		float totalUnigramSentenceProbability = 0;
		float totalBigramSentenceProbability = 0;
		int totalSentenceCount = 0;
		// Stores the original and log form respectively of a sentence's probability. 
		float[] rawAndLogProbability = new float[2];
		
		// Retrieve a list of all sentences from the training dataset.
		for (CoreMap sentences : doc.get(SentencesAnnotation.class) )
		{
			totalSentenceCount++;
			// compute the unigram log probability of the sentence.
			rawAndLogProbability = findUnigramProbability(sentences);
			unigramSentenceProbability = rawAndLogProbability[0];
			totalUnigramPerplexityProbability += rawAndLogProbability[1]; // log probability should come out from here. should be + not *
			totalUnigramSentenceProbability += unigramSentenceProbability;
			
			// compute the bigram log probability of the sentence.
			rawAndLogProbability = findBigramProbability(sentences);
			bigramSentenceProbability = rawAndLogProbability[0];
			totalBigramPerplexityProbability += rawAndLogProbability[1];
			totalBigramSentenceProbability += bigramSentenceProbability;
			
			// Append the log probabilities and sentence to a list mapping them together.
			float[] probabilities = { unigramSentenceProbability, bigramSentenceProbability };
			documentSentences.put(sentences.toString(), probabilities);
		}
		
		// Convert the log sentence probabilities into their raw form before finally using it to compute perplexity
		totalUnigramPerplexityProbability = (float) Math.exp(totalUnigramPerplexityProbability);
		totalBigramPerplexityProbability = (float) Math.exp(totalBigramPerplexityProbability);
		
		// Sum up all of the unigram and bigram probabilities in the dataset and average them.
		averageUnigramSentenceProbability(totalUnigramSentenceProbability, totalSentenceCount);
		averageBigramSentenceProbability(totalBigramSentenceProbability, totalSentenceCount);
	}
	
	
	/**
	 * Helper method that computes the sentence-probability of 
	 * the bigrams in a given sentence from in a dataset. 
	 * New bigrams discovered in a new dataset, which are
	 * not present in the training dataset used, are assumed
	 * to have a log-probability of 0.
	 * 
	 * @param sentence	the given sentence to compute its bigram sentence-probability.
	 * 
	 * @return	the bigram sentence-probability of the given sentence.
	 */
	private float[] findBigramProbability(CoreMap sentence)
	{
		String[] words;
		// The first and second words in the bigram.
		String firstWord, secondWord; 
		// The log probability of the bigram.
		float logSentenceProbability = 0;
		// The original form of the probability when converted.
		float originalSentenceProbability = 0;
		
    	for (String bigram : StringUtils.getNgramsString(FileHandler.getInstance().appendToSentence(sentence), 2, 2) )
    	{
    		words = bigram.split(" ");
    		firstWord = words[0]; secondWord = words[1];

    		for (String word : bigramCount.keySet() )
    		{
    			if( bigramCount.get(word).firstWord.equals(firstWord) && bigramCount.get(word).secondWord.equals(secondWord))
    			{
    				logSentenceProbability += bigramCount.get(word).probability;
  
    			}  			
    		}
    	}
    	originalSentenceProbability = (float) Math.exp(logSentenceProbability);
    	float[] probabilities = { originalSentenceProbability, logSentenceProbability };
		return probabilities;
	}
	
	
	/**
	 * Helper method that computes the sentence-probability of 
	 * the unigrams in a given sentence from in a dataset. 
	 * New unigrams discovered in a new dataset, which are
	 * not present in the training dataset used, are assumed
	 * to have a log-probability of 0.
	 * 
	 * @param sentence	the given sentence to compute its unigram sentence-probability.
	 * 
	 * @return	the unigram sentence-probability of the given sentence.
	 * 
	 */
	private float[] findUnigramProbability(CoreMap sentence)
	{
		// The log probability of the bigram.
		float logSentenceProbability = 0;
		// The original form of the probability when converted.
		float originalSentenceProbability = 0;
		
		
    	for (String unigram : StringUtils.getNgramsString(FileHandler.getInstance().appendPrefixToSentence(sentence), 1, 1) )
    	{
    		// If the unigram is not contained in the language model then it is
    		// brand new and assigned a 0-log probability.
    		if ( allWordTokens.keySet().contains(unigram))
    		{
    			// Get the log-probability of the unigram and add it in the overall sentence-probability.
    			logSentenceProbability += allWordTokens.get(unigram).probability;
    		}	
    	}
    	originalSentenceProbability = (float) Math.exp(logSentenceProbability);
    	float[] probabilities = { originalSentenceProbability, logSentenceProbability };
		return probabilities;
	}
	
	
	/**
	 * Helper method that computes the average unigram sentence
	 * probability of a dataset.
	 * 
	 * @param totalUnigramSP	the total unigram sentence probability of the dataset.
	 * 
	 * @param totalSentenceNumber	the total number of sentences in the dataset.
	 * 
	 * @return	the average unigram sentence probability of the dataset.
	 * 
	 */
	private float averageUnigramSentenceProbability(float totalUnigramSP, int totalSentenceNumber)
	{ 
		// Store in a separate data structure and return the average unigram sentence probability.
		avgUnigramSentenceProbability = (totalUnigramSP/totalSentenceNumber);
		return avgUnigramSentenceProbability;
	}
	
	
	/**
	 * Helper method that computes the average bigram sentence
	 * probability of a dataset.
	 * 
	 * @param totalBigramSP	the total bigram sentence probability of the dataset.
	 * 
	 * @param totalSentenceNumber	the total number of sentences in the dataset.
	 * 
	 * @return	the average unigram sentence probability of the dataset.
	 * 
	 */
	private float averageBigramSentenceProbability(float totalBigramSP, int totalSentenceNumber)
	{ 
		// Store in a separate data structure and return the average biigram sentence probability.
		avgBigramSentenceProbability = (totalBigramSP/totalSentenceNumber);
		return avgBigramSentenceProbability;
	}
	
	
	/**
	 * Helper method that computes the unigram and 
	 * bigram perplexities of a given test dataset.
	 * 
	 * @param doc	the given test dataset whose perplexity is to be computed.
	 * 
	 * @return	the unigram and bigram perplexities of the given dataset.
	 */
	private float[] computeTestDataPerplexityOperation(Annotation doc)
	{ 
		float unigramPerplexity = 0;
		float bigramPerplexity = 0;
		
		// Get the number of tokens/per sentence in the given test dataset 
		testDataSentenceCounter(doc);
		float Ncount = new Integer(testDataTokens + testDataSentences).floatValue();
		
		// Compute the unigram and bigram perplexities of the given test dataset.
		unigramPerplexity = computePerplexity(totalUnigramPerplexityProbability, Ncount);
		bigramPerplexity = computePerplexity(totalBigramPerplexityProbability, Ncount);
		float[] testDataPerplexity = { unigramPerplexity, bigramPerplexity };
		return testDataPerplexity;
	} 

	
	/**
	 * Helper method that computes the perplexity given
	 * a probability and the N-count.
	 * 
	 * @param probability	the probability of a sequence of one of more words.
	 * 
	 * @param nCount	the number of words.
	 * 
	 * @return	the perplexity of the sequence of words.
	 * 
	 */
	private float computePerplexity(float probability, float nCount)
	{
		float perplexity = 0;
		float power = (1/nCount);
		float base = (1/probability);
		perplexity = (float) Math.pow(base, power);
		return perplexity;
	}
	
	
	/**
	 * Helper method that sums the integer values
	 * in a given HashMap.
	 * 
	 * @param items	the HashMap whose values are to be summed up.
	 * 
	 * @return	the total sum of the integer values in the given HashMap.
	 */
	private int sumMapValues(HashMap<String, WordToken> items)
	{
		int sum = 0;
		for (String key : items.keySet() ) { sum += items.get(key).count;  }
		
		return sum;
	}
	
	
	/**
	 * Helper method that splits
	 * a list of sentences into
	 * their individual bigrams.
	 * 
	 * @param sentenceList	 the list of sentences to be split into bigram tokens.
	 * 
	 * @return	a list containing all bigram tokens split from a list of sentences.
	 */
	private ArrayList<String> splitTokens(String[] sentenceList )
	{
		Collection<String> bigramsList = new ArrayList<String>();
		ArrayList<String> tokens = new ArrayList<String>();
		for (String sentence : sentenceList)
		{
			bigramsList = StringUtils.getNgramsString(sentence, 2, 2);
			for (String token : bigramsList)
			{
				tokens.add(token);
			}
		}
		return tokens;
	}
	
}
