import * as fmt from 'format-imports';

import {
  FormatFileArgs,
  MessageWriter,
  Plugin,
  PluginFactory,
  PluginState,
  Request,
  Response,
} from './types';

class FormatImportsPlugin implements Plugin {
  onMessage(p: string, writer: MessageWriter) {
    const request: Request = JSON.parse(p);
    const response: Response = {
      ...FormatImportsPlugin.handleRequest(request),
      request_seq: request.seq,
    };
    writer.write(JSON.stringify(response));
  }

  private static handleRequest(request: Request | undefined) {
    if (!request) return { error: 'Invalid request' };
    const { command } = request;
    try {
      switch (command) {
        case 'formatSourceFromFile':
          return FormatImportsPlugin.formatSourceFromFile(request.arguments as FormatFileArgs);
        default:
          return { error: `Unknown command '${command}'` };
      }
    } catch (e: unknown) {
      return { error: e instanceof Error ? e.message : `${e}` };
    }
  }

  private static formatSourceFromFile(args: FormatFileArgs | undefined) {
    if (!args || !args.filePath) return { error: 'Missing file path' };
    const { filePath, source } = args;
    // TODO: Base config
    const config = fmt.resolveConfigForFile(filePath);
    if (fmt.isFileExcludedByConfig(filePath, config)) return;
    return { result: fmt.formatSourceFromFile(source, filePath, config) };
  }
}

class FormatImportsPluginFactory implements PluginFactory {
  create(_: PluginState) {
    return { languagePlugin: new FormatImportsPlugin() };
  }
}

export const factory = new FormatImportsPluginFactory();
