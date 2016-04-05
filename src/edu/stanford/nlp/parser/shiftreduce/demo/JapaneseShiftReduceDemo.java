package edu.stanford.nlp.parser.shiftreduce.demo;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.logging.Redwood;

import java.util.ArrayList;
import java.util.List;

import org.chasen.mecab.Node;
import org.chasen.mecab.Tagger;

/**
 * Demonstrates how to first use a Japanese tagger, then use the
 * ShiftReduceParser. Note that ShiftReduceParser will not work
 * on untagged text.
 *
 * @author Mike Tian-Jian Jiang
 */
public class JapaneseShiftReduceDemo  {

    private static Redwood.RedwoodChannels log = Redwood.channels(JapaneseShiftReduceDemo.class);

    public static void main(String[] args) {
        String modelPath = "ja.beam.rightmost.model.ser.gz";

        for (int argIndex = 0; argIndex < args.length; ) {
            switch (args[argIndex]) {
                case "-model":
                    modelPath = args[argIndex + 1];
                    argIndex += 2;
                    break;
                default:
                    throw new RuntimeException("Unknown argument " + args[argIndex]);
            }
        }

        String text = "太郎は二郎にこの本を渡した。";

        System.loadLibrary("mecab-java");
        ShiftReduceParser model = ShiftReduceParser.loadModel(modelPath);

        Tagger tagger = new Tagger();
        Node node = tagger.parseToNode(text);
        List<TaggedWord> tagged = new ArrayList<>();
        for (; node != null; node = node.getNext()) {
            String word = node.getSurface();
            String tag = node.getFeature().split(",")[0];
            log.info(word);
            log.info(tag);
            tagged.add(new TaggedWord(word, tag));
        }
        Tree tree = model.apply(tagged);
        log.info(tree);
    }
}
