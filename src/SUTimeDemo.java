import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.util.CoreMap;

public class SUTimeDemo {

  /** Example usage:
   *  java SUTimeDemo "Three interesting dates are 18 Feb 1997, the 20th
   of july and 4 days from today."
   *
   *  @param args Strings to interpret
   */
  public static void main(String[] args) {
    AnnotationPipeline pipeline = new AnnotationPipeline();
    pipeline.addAnnotator(new PTBTokenizerAnnotator(false));
    pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
    
    String modelDir = "models/";

    //MaxentTagger tagger = new MaxentTagger(modelDir + "pos-tagger/english-bidirectional/english-bidirectional-distsim.tagger");
    //pipeline.addAnnotator(new POSTaggerAnnotator(tagger));
    
    String sutimeRules = modelDir + "/sutime/defs.sutime.txt,"
        + modelDir + "/sutime/english.holidays.sutime.txt,"
        + modelDir + "/sutime/english.sutime.txt";
    Properties props = new Properties();
    props.setProperty("sutime.rules", sutimeRules);
    props.setProperty("sutime.binders", "0");
    pipeline.addAnnotator(new TimeAnnotator("sutime", props));

    String text = "Last summer, they met every Tuesday afternoon, from 1:00 pm to 3:00 pm.";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.DocDateAnnotation.class, SUTime.getCurrentTime().toString());
    pipeline.annotate(annotation);
    
    //System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
    
    List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
    for (CoreMap cm : timexAnnsAll) {
      List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
      System.out.println(cm + " [from char offset " +
          tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
          " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
          " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
     
      System.out.println("--");
    }
  }

}