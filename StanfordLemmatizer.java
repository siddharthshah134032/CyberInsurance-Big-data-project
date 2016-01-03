package textmining;



import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;



public class StanfordLemmatizer {
    
    private static StanfordLemmatizer instance;
    protected StanfordCoreNLP pipeline;
    
    private StanfordLemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma,parse");
        props.put("clean.xmltags", "*");
        
        // StanfordCoreNLP loads a lot of models, so you probably
        // only want to do this once per execution
        this.pipeline = new StanfordCoreNLP(props);
    }
    
    public static StanfordLemmatizer getInstance(){
        if(instance == null){
            instance = new StanfordLemmatizer();
        }
        return instance;
    }
    
    public List<String> lemmatize(String documentText)
    {
        List<String> lemmas = new LinkedList<String>();
        
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        
        // run all Annotators on this text
        this.pipeline.annotate(document);
        
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the list of lemmas
                lemmas.add(token.get(LemmaAnnotation.class));
            }
            
            Tree tree = sentence.get(TreeAnnotation.class);
            
            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
            
            for (IndexedWord x: dependencies.getLeafVertices()) {
                //System.out.print(x.word() + " ");
            }
           // System.out.println(dependencies.edgeCount());
        }
        
        return lemmas;
    }
    
    public List<String> lemmatize(List<String> tokens) {
        List<String> lemmas = new ArrayList<String>();
    	for (String t: tokens) {
    		if(LemmaStore.getInstance().isLemmaAvailable(t)) {
            	lemmas.add(LemmaStore.getInstance().getLemma(t));
            	//System.out.println("Existing lemma");
            } else {
            	List<String> results = lemmatize(t);
            	if(results.size()>0) {
            		String lemma = results.get(0);
                	if(lemma!="" && lemma!=null) {
                		lemmas.add(lemma);
                    	LemmaStore.getInstance().addLemma(t, lemma);
                    	//System.out.println("New lemma");
                	}
            	}
            }
    	}
        return lemmas;
    }
    
    public String run(String line) {
        LexicalizedParser lp = LexicalizedParser.loadModel(
                                                           "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz",
                                                           "-maxLength", "80", "-retainTmpSubcategories");
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        // Uncomment the following line to obtain original Stanford Dependencies
        // tlp.setGenerateOriginalDependencies(true);
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        Tree parse = lp.apply(Sentence.toWordList(line.split(" ")));
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        Collection<TypedDependency>	tdl = gs.typedDependenciesCCprocessed();
        String deps="";
        for (TypedDependency t: tdl) {
            if(t.gov().pennString().contains("Exploit")) {
                deps+=t.dep().value() + " ";
            } 
            if (t.gov().pennString().contains("Vulnerability")) {
                deps+=t.dep().value()+" ";
            }
        }
        System.out.println(deps);
        return deps;
    }
}



