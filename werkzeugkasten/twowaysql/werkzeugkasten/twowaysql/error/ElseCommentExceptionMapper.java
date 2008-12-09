package werkzeugkasten.twowaysql.error;

import werkzeugkasten.twowaysql.grammar.TwoWaySqlParser;

public class ElseCommentExceptionMapper extends AbstractExceptionMapper {

	public ElseCommentExceptionMapper() {
		add(new NoViableAltHandler(Messages.LABEL_ELSECOMMENT,
				Messages.VIABLE_ELSECOMMENT));
		add(new MismatchedTokenHandler() {
			protected String selectExpected(int expecting) {
				return "ELSE";
			}
		});
		add(new MissingTokenHandler() {
			protected String selectExpected(int expecting) {
				switch (expecting) {
				case TwoWaySqlParser.C_ED: {
					return "*/";
				}
				case TwoWaySqlParser.C_LN_ED: {
					return "\\n";
				}
				default: {
					throw new IllegalStateException();
				}
				}
			}
		});
	}
}