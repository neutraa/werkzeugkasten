package werkzeugkasten.twowaysql.error;

import werkzeugkasten.twowaysql.grammar.TwoWaySqlParser;

public class BlockCommentExceptionMapper extends AbstractExceptionMapper {

	public BlockCommentExceptionMapper() {
		add(new EarlyExitHandler(Messages.LABEL_BLOCKCOMMENT,
				Messages.REQUIRED_BLOCKCOMMENT));
		add(new MissingTokenHandler() {
			protected String selectExpected(int expecting) {
				switch (expecting) {
				case TwoWaySqlParser.C_ST: {
					return "/*";
				}
				case TwoWaySqlParser.C_ED: {
					return "*/";
				}
				default: {
					throw new IllegalStateException();
				}
				}
			}
		});
	}
}