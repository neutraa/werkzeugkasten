package twowaysqleditor;

import static twowaysqleditor.Constants.*;
import static twowaysqleditor.rules.SqlPartitionScanner.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import twowaysqleditor.contentassist.CommentContentAssistProcessor;
import twowaysqleditor.contentassist.DefaultContentAssistProcessor;
import twowaysqleditor.contentassist.IfContentAssistProcessor;
import twowaysqleditor.formatter.OneShotContentFormatter;
import twowaysqleditor.hyperlink.ELHyperlinkDetector;
import twowaysqleditor.rules.KeywordScanner;
import twowaysqleditor.rules.NonRuleBasedDamagerRepairer;
import twowaysqleditor.rules.SqlPartitionScanner;
import twowaysqleditor.util.ColorManager;

public class SqlConfiguration extends SourceViewerConfiguration {

	protected ColorManager colorManager;
	protected EditorContext context;
	protected KeywordScanner keywordScanner;
	protected NonRuleBasedDamagerRepairer commentScanner;
	protected IContentFormatter contentFormatter;
	protected DefaultContentAssistProcessor defaultAssist;
	protected CommentContentAssistProcessor commentAssist;
	protected IfContentAssistProcessor ifAssist;

	public SqlConfiguration(ColorManager manager, EditorContext context) {
		this.colorManager = manager;
		this.context = context;
	}

	protected KeywordScanner getKeywordScanner() {
		if (keywordScanner == null) {
			keywordScanner = new KeywordScanner(this.colorManager);
			keywordScanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(GREEN))));
		}
		return keywordScanner;
	}

	protected NonRuleBasedDamagerRepairer getCommentScanner() {
		if (commentScanner == null) {
			commentScanner = new NonRuleBasedDamagerRepairer(new TextAttribute(
					colorManager.getColor(GREEN)));
		}
		return commentScanner;
	}

	protected IContentFormatter getContentFormatter() {
		if (contentFormatter == null) {
			contentFormatter = new OneShotContentFormatter();
		}
		return contentFormatter;
	}

	protected DefaultContentAssistProcessor getDefaultAssist() {
		if (defaultAssist == null) {
			defaultAssist = new DefaultContentAssistProcessor(context);
		}
		return defaultAssist;
	}

	protected CommentContentAssistProcessor getCommentAssist() {
		if (commentAssist == null) {
			commentAssist = new CommentContentAssistProcessor(context);
		}
		return commentAssist;
	}

	protected IfContentAssistProcessor getIfAssist() {
		if (ifAssist == null) {
			ifAssist = new IfContentAssistProcessor(context);
		}
		return ifAssist;
	}

	protected static final String[] CONTENT_TYPES;
	static {
		List<String> list = new ArrayList<String>();
		list.add(IDocument.DEFAULT_CONTENT_TYPE);
		list.addAll(Arrays.asList(SqlPartitionScanner.PARTITIONS));
		CONTENT_TYPES = list.toArray(new String[list.size()]);
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return CONTENT_TYPES;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				getKeywordScanner());
		for (String type : new String[] { SQL_IF, SQL_ELSE, SQL_BEGIN, SQL_END }) {
			reconciler.setDamager(dr, type);
			reconciler.setRepairer(dr, type);
		}

		NonRuleBasedDamagerRepairer ndr = getCommentScanner();
		for (String type : new String[] { SQL_LINE_COMMENT, SQL_COMMENT }) {
			reconciler.setDamager(ndr, type);
			reconciler.setRepairer(ndr, type);
		}

		return reconciler;
	}

	@Override
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		return getContentFormatter();
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();

		assistant.setContentAssistProcessor(getDefaultAssist(),
				IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(getCommentAssist(), SQL_COMMENT);
		assistant.setContentAssistProcessor(getIfAssist(), SQL_IF);
		assistant.enableAutoActivation(true);

		return assistant;
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer != null) {
			return new IHyperlinkDetector[] { new ELHyperlinkDetector(context) };
		}
		return null;
	}
}
