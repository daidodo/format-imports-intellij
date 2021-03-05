export interface MessageWriter {
  write(answer: string): void;
}

export interface PluginState {
  pluginName: string;
  pluginPath?: string;
  sessionId: string;
}

export interface Request {
  seq: number;
  type: string;
  command: string;
  arguments?: Record<string, any>;
}

export interface Response {
  request_seq: number;
  error: string;
  result?: string;
}

export interface FormatFileArgs {
  filePath: string;
  source: string;
}
