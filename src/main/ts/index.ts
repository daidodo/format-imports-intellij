import {
  formatSourceFromFile,
  isFileExcludedByConfig,
  resolveConfigForFile,
} from 'format-imports';

import {
  FormatFileArgs,
  MessageWriter,
  PluginState,
  Request,
  Response, Plugin, PluginFactory
} from './types';

class FormatImportsPlugin implements Plugin {
  onMessage(p: string, writer: MessageWriter) {
    const request: Request = JSON.parse(p);
    const response: Response = {
      ...this.handleRequest(request),
      request_seq: request.seq,
    };
    writer.write(JSON.stringify(response));
  }

  private handleRequest(request: Request | undefined) {
    if (!request) return {error: 'Invalid request'};
    const {command} = request;
    try {
      switch (command) {
        case 'formatSourceFromFile':
          return this.formatSourceFromFile(request.arguments as FormatFileArgs);
        default:
          return {error: `Unknown command '${command}'`};
      }
    } catch (e: unknown) {
      return {error: e instanceof Error ? e.message : `${e}`};
    }
  }

  private formatSourceFromFile(args: FormatFileArgs | undefined) {
    if (!args || !args.filePath) return {error: 'Missing file path'};
    const {filePath, source} = args;
    // TODO: Base config
    const config = resolveConfigForFile(filePath);
    if (isFileExcludedByConfig(filePath, config)) return;
    return {result: formatSourceFromFile(source, filePath, config)};
  }
}

class FormatImportsPluginFactory implements PluginFactory {
  create(_: PluginState) {
    return {languagePlugin: new FormatImportsPlugin()};
  }
}

export let factory = new FormatImportsPluginFactory();