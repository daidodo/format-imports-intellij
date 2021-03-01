import fs from 'fs';
import { formatSourceFromFile, isFileExcludedByConfig, resolveConfigForFile } from 'format-imports';

interface MessageWriter {
  write(answer: string): void;
}

interface PluginState {
  pluginName: string;
  pluginPath?: string;
  sessionId: string;
}

interface Request {
  readonly seq: number;
  readonly type: string;
  readonly command: string;
  readonly arguments?: Record<string, any>;
}

interface FormatFileArgs {
  filePath: string;
}

class FormatImportsPlugin {
  onMessage(p: string, writer: MessageWriter) {
    console.log(`p: ${p}`);
    const req: Request = JSON.parse(p);
    console.log(`req: ${req}`);
    writer.write(JSON.stringify({ ...this.handleRequest(req), request_seq: req.seq }));
  }

  handleRequest(req: Request) {
    try {
      switch (req.command) {
        case 'formatFile':
          return this.formatFile(req.arguments as FormatFileArgs);
        default:
          return { error: `Unknown command '${req.command}'` };
      }
    } catch (e: unknown) {
      return { error: e instanceof Error ? e.message : `${e}` };
    }
  }

  private formatFile(args: FormatFileArgs | undefined): { error?: string; result?: string } {
    console.log(`args: ${args}`);
    if (!args || !args.filePath) return { error: 'Missing file path' };
    const { filePath } = args;
    // TODO: Base config
    const config = resolveConfigForFile(filePath);
    console.log(`config: ${config}`);
    if (isFileExcludedByConfig(filePath, config)) return {};
    const source = fs.readFileSync(filePath).toString();
    console.log(`source: ${source}`);
    return { result: formatSourceFromFile(source, filePath, config) };
  }
}

class FormatImportsPluginFactory {
  create(state: PluginState) {
    return { languagePlugin: new FormatImportsPlugin() };
  }
}

export let factory = new FormatImportsPluginFactory();
