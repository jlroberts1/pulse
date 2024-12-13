import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        HelperKt.doInitKoin()
        NapierProxyKt.debugBuild()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}