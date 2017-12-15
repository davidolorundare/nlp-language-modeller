package languagemodel.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import languagemodel.compute.LanguageModeler;
import languagemodel.structures.AnalyzedData;
import languagemodel.utils.FileHandler;
import languagemodel.utils.OutputPrinter;


/**
 * 					CSC 594 ASSIGNMENT 2: LANGUAGE MODELS
 * 
 * This is the entry main class for the unigram and bigram language modeling program.
 *
 * :EXAMPLE USAGE:
 * 
 * Compile the .java files then,
 * From the command line run:
 * 
 * >> java -classpath .:stanford-corenlp-3.7.0.jar LanguageModelerMain <input_file_containing_training-dataset> <input_file_containing_test-dataset> <output_file_to_store_language_model_evaluation-analysis> <switches: -P | -S | -G 2>
 * 
 * 
 * 		:PROGRAM OVERVIEW:
 * 
 * The program evaluates the language model built from the input training dataset file,
 * using the dataset in the test file. Evaluations outputted consist o:
 *  * the unigrams and bigrams sentence-probabilities of the test dataset, 
 *  * the average unigram and bigram probabilities of the dataset, 
 *  * if the '-P' switch is included; the unigram and bigram perplexities of the document are computed,
 *  * if the '-S' switch is included; Laplace-smoothing is used when building the bigram language model, 
 *  * if the '-G x' switch is included; x number of sentences are randomly generated along with the overall output. 
 * 
 * 
 * 		:PROGRAM OPERATION/STRUCTURE:
 * 
 * The implementation uses a pipe-design and code is split into: 
 * 
 * INPUT --> LANGUAGE MODELLING (with or without smoothing) -> ANALYSIS AND COMPUTATION --> OUTPUT (with or without Perplexity-computation and Random sentence Generation)
 * 
 * The program variables and component are first initialized,
 * then the input text is taken from the training data file provided.
 * The language models (unigram and bigram) are built using this data,
 * and evaluated on various parameters using the separate test dataset 
 * text provided, the results of the evaluation and analysis operations 
 * performed are outputted to the console, and also stored in the specified 
 * external output text file.
 *  
 * 
 * @author David Olorundare
 *
 */
public class LanguageModelerMain 
{
	public static void main(String[] args) throws IOException
	{
		
		//==================== INITIALIZATION OF PROGRAM COMPONENTS ========================================
		
		HashMap<String, Integer> languageOperations = new HashMap<String, Integer>();
		FileHandler textData = FileHandler.getInstance();
		LanguageModeler textComputation = LanguageModeler.getInstance();
		OutputPrinter output = OutputPrinter.getInstance();
		AnalyzedData languageAnalysis = new AnalyzedData();
			
		//============================ INPUT FILE HANDLING AND LANGUAGE MODELING ANALYSIS =========================================
		
		// Take input file from the command line, operate on it, and store results in the output file.
		if (args.length >= 2)
		{
			// Set the training dataset, test dataset, and output-results file locations.
			textData.setTrainDataFilePath(args[0]);
			textData.setTestDataFilePath(args[1]);
			textData.setOutputResultFilePath(args[2]);
			
            //	Extra Functionality (compute perplexity, add smoothing, generate-random-sentences)
			if (args.length > 3)
			{
				for (int index = 3; index < args.length; index++ )
				{
					// Include perplexity computation.
					if (args[index].equals("-P")){ languageOperations.put("perplexity", 1); }
					// Include Laplace smoothing.
					if (args[index].equals("-S")){ languageOperations.put("smoothing", 1); }
					// Include random sentence generation.
					if (args[index].equals("-G")){ int numberToGenerate = Integer.parseInt(args[index+1]); languageOperations.put("generator", numberToGenerate); }
				}
			}
			
			try 
			{
				// Load the input text, build the language models, perform the operations, and return the results.
				languageAnalysis = textData.loadAndCompute(textComputation, languageOperations); 
			
		   //============================== RESULTS-PRINTING  ======================================================
				
				// Print out language model analysis of the given input text and stores it in an external file.
				output.printAnalysisToScreen(languageAnalysis);
		        
		   //====================================================================================================
			}
			catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace(); }	
		}
		else 
		{
			// Show the user some Usage-info.
			System.out.println(":Usage: ./java -classpath .:stanford-corenlp-3.7.0.jar LanguageModelerMain <input_file_containing_training-text> "
					+ "<input_containing_testing-text> <output_file_to_store_language_model_results>"
					+ " <compute_perplexity-option | add_smoothing-option | "
					+ "<generate sentences> <no. of sentences to generate>");
			return;
		}
	}
}
