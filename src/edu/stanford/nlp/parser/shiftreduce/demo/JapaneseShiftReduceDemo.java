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
        boolean callMeCab = false;

        for (int argIndex = 0; argIndex < args.length; ) {
            switch (args[argIndex]) {
                case "-model":
                    modelPath = args[argIndex + 1];
                    argIndex += 2;
                    break;
                case "-mecab":
                    callMeCab = true;
                    argIndex += 2;
                    break;
                default:
                    throw new RuntimeException("Unknown argument " + args[argIndex]);
            }
        }

        RedwoodConfiguration.empty().capture(System.err).apply();
        ShiftReduceParser model = ShiftReduceParser.loadModel(modelPath);
        RedwoodConfiguration.current().clear().apply();

        Tagger tagger = null;
        if (callMeCab) {
            System.loadLibrary("mecab-java");
            tagger = new Tagger();
            tagger.parse("太郎は二郎にこの本を渡した。");
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String text = scanner.nextLine();
            List<TaggedWord> taggedWords = new ArrayList<>();
            if (callMeCab) {
                Node node = tagger.parseToNode(text);
                if (null == node) {
                    continue;
                }
                node = node.getNext();
                for (; node.getStat() != MeCabConstants.MECAB_EOS_NODE; node = node.getNext()) {
                    String word = node.getSurface();
                    String tag = node.getFeature().split(",")[0];
                    taggedWords.add(new TaggedWord(word, tag));
                }
            } else {
                String[] tokens = text.split(" ");
                for (String token: tokens) {
                    TaggedWord taggedWord = new TaggedWord();
                    taggedWord.setFromString(token);
                    taggedWords.add(taggedWord);
                }
            }
            Tree tree = model.apply(taggedWords);
            System.out.println(tree);
        }

    }
}
