import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        AppModuleKt.initializeKoinIos()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}