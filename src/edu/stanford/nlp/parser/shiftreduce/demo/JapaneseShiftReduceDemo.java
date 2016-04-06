package edu.stanford.nlp.parser.shiftreduce.demo;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import org.chasen.mecab.MeCabConstants;
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

        System.loadLibrary("mecab-java");

        RedwoodConfiguration.empty().capture(System.err).apply();
        ShiftReduceParser model = ShiftReduceParser.loadModel(modelPath);
        RedwoodConfiguration.current().clear().apply();

        Tagger tagger = new Tagger();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String text = scanner.nextLine();
            Node node = tagger.parseToNode(text);
            if (null == node) {
                continue;
            }
            node = node.getNext();

            List<TaggedWord> tagged = new ArrayList<>();
            for (; node.getStat() != MeCabConstants.MECAB_EOS_NODE; node = node.getNext()) {
                String word = node.getSurface();
                String tag = node.getFeature().split(",")[0];
                tagged.add(new TaggedWord(word, tag));
            }
            Tree tree = model.apply(tagged);
            System.out.println(tree);
        }

    }
}
